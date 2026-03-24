package com.shop.service.user.member;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Member;
import com.shop.domain.MemberRole;
import com.shop.dto.user.auth.RegisterRequestDto;
import com.shop.mapper.user.MemberMapper;
import com.shop.service.user.roulette.RouletteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    // ★ 신규가입 쿠폰 자동 지급을 위해 RouletteService 주입
    private final RouletteService rouletteService;

    // memberId로 회원 + 권한 목록 함께 조회 (JWT 인증용)
    @Override
    public Member readOneMemberWithRoles(String memberId) throws Exception {
        return memberMapper.readOneMemberWithRoles(memberId);
    }

    // memberId로 회원 단건 조회
    @Override
    public Member readOneMember(String memberId) throws Exception {
        return memberMapper.readOneMember(memberId);
    }

    // 리프레시 토큰 업데이트 (로그인/로그아웃 시)
    @Override
    @Transactional
    public void updateRefreshToken(String memberId, String refreshToken) throws Exception {
        memberMapper.updateRefreshToken(memberId, refreshToken);
    }

    // 일반 회원가입
    @Override
    @Transactional
    public void register(RegisterRequestDto dto) throws Exception {
        // 비밀번호 BCrypt 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        Member member = new Member();
        member.setMemberId(dto.getMemberId());
        member.setPassword(encodedPassword);
        member.setName(dto.getName());
        member.setNickName(dto.getNickName());
        member.setEmail(dto.getEmail());
        member.setPhoneNumber(dto.getPhoneNumber());
        member.setZipCode(dto.getZipCode());
        member.setBasicAddress(dto.getBasicAddress());
        member.setDetailAddress(dto.getDetailAddress());
        member.setGender(dto.getGender());
        member.setBirthday(dto.getBirthday());
        member.setBankName(dto.getBankName());
        member.setBankCode(dto.getBankCode());
        member.setAccountHolderName(dto.getAccountHolderName());

        // 회원 DB 저장 (selectKey로 memberNo 자동 세팅)
        memberMapper.register(member);

        // USER 권한 부여
        MemberRole role = new MemberRole();
        role.setRoleName("USER");
        role.setMemberNo(member.getMemberNo());
        memberMapper.insertRole(role);

        // ★ 신규가입 쿠폰 자동 지급 (3,000원 할인쿠폰 365일 유효)
        rouletteService.issueSignupCoupon(member.getMemberNo());
    }

    // 관리자 계정 등록 (테스트/초기 세팅용)
    @Override
    @Transactional
    public void insertAdmin(Member member) throws Exception {
        MemberRole mr1 = new MemberRole();
        mr1.setMemberRoleNo(9999998L);
        mr1.setMemberNo(member.getMemberNo());
        mr1.setRoleName("USER");

        MemberRole mr2 = new MemberRole();
        mr2.setMemberRoleNo(9999999L);
        mr2.setMemberNo(member.getMemberNo());
        mr2.setRoleName("ADMIN");

        memberMapper.insertAdmin(member);
        memberMapper.insertAdminRole(mr1);
        memberMapper.insertAdminRole(mr2);
    }

    // 카카오 로그인 - 기존 회원이면 조회, 신규면 자동 가입
    @Override
    public Member getOrCreateKakaoMember(Map<String, Object> kakaoUserInfo) throws Exception {
        Object idObj = kakaoUserInfo.get("id");
        if (idObj == null) {
            throw new RuntimeException("카카오 사용자 id가 없습니다.");
        }

        String kakaoId = String.valueOf(idObj);
        String memberId = "k_" + kakaoId;

        // 기존 카카오 회원인지 확인
        Member existingMember = null;
        try {
            existingMember = memberMapper.readOneMember(memberId);
        } catch (Exception e) {
        }

        // 이미 가입된 카카오 회원이면 권한 포함 조회 후 반환
        if (existingMember != null) {
            return memberMapper.readOneMemberWithRoles(memberId);
        }

        // 신규 카카오 회원 자동 가입
        String tempPassword = java.util.UUID.randomUUID().toString();
        String nickname = "kakao_" + kakaoId;

        Member newMember = new Member();
        newMember.setMemberId(memberId);
        newMember.setPassword(passwordEncoder.encode(tempPassword));
        newMember.setName(nickname);
        newMember.setNickName(nickname);
        newMember.setProvider("KAKAO");

        memberMapper.insertKakaoMember(newMember);
        memberMapper.insertRoleByMemberNo(newMember.getMemberNo(), "USER");

        return memberMapper.readOneMemberWithRoles(memberId);
    }

    // ================================================
    // 구매 횟수 관련
    // ================================================

    // 구매 횟수 증가 (결제 완료 시 호출)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberGrade(Long memberNo) throws Exception {
        this.increasePurchaseCount(memberNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increasePurchaseCount(Long memberNo) throws Exception {
        if (memberNo == null) return;
        memberMapper.incrementPurchaseCount(memberNo);
        log.info("(MemberServiceImpl) 회원 {}번 구매 횟수 1 증가 완료", memberNo);
    }

    // 구매 횟수 감소 (환불/취소 시 호출)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decreasePurchaseCount(Long memberNo) throws Exception {
        if (memberNo == null) return;
        memberMapper.decrementPurchaseCount(memberNo);
        log.info("(MemberServiceImpl) 회원 {}번 구매 횟수 1 차감 완료", memberNo);
    }

    // 구매 횟수 기반 등급 계산
    public String determineGrade(int count) {
        if (count >= 30) return "VVIP";
        if (count >= 20) return "VIP";
        if (count >= 10) return "GOLD";
        if (count >= 5)  return "SILVER";
        return "BASIC";
    }
}