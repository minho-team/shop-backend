package com.shop.service.user.member;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Member;
import com.shop.domain.MemberRole;
import com.shop.dto.user.auth.RegisterRequestDto;
import com.shop.dto.user.member.MemberUpdateRequestDTO;
import com.shop.mapper.user.MemberMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.service.user.roulette.RouletteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	private final RouletteService rouletteService;
	private final OrdersMapper ordersMapper;

	// memberId로 회원 + 권한 목록 함께 조회 (JWT 인증용)
	@Override
	public Member readOneMemberWithRoles(String memberId) throws Exception {
	    Member member = memberMapper.readOneMemberWithRoles(memberId);
	    
	    if (member != null) {
	        // 1. 누적 금액 계산
	        Long total = ordersMapper.selectTotalPurchaseAmount(member.getMemberNo());
	        long totalSpent = (total != null ? total : 0L);
	        member.setTotalSpent(totalSpent);
	        
	        // 2. 누적 금액에 따른 새로운 등급 결정 (New Grade Calculation)
	        String newGrade = "GENERAL"; // 기본 등급
	        if (totalSpent >= 1000000) {
	            newGrade = "VVIP";
	        } else if (totalSpent >= 500000) {
	            newGrade = "VIP";
	        } else if (totalSpent >= 300000) {
	            newGrade = "GOLD";
	        }

	        // 3. 현재 DB 등급과 계산된 등급이 다를 경우에만 DB 업데이트
	        if (!newGrade.equals(member.getGrade())) {
	            memberMapper.updateGrade(member.getMemberNo(), newGrade);
	            member.setGrade(newGrade); // 현재 객체의 등급도 변경
	            log.info("[등급 상향] 회원: {}, 기존: {}, 변경: {}", memberId, member.getGrade(), newGrade);
	        }
	        
	        log.info("[내 정보 조회] 회원: {}, 누적 금액: {}원, 현재 등급: {}", memberId, member.getTotalSpent(), member.getGrade());
	    }
	    
	    return member;
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

		// ★ 신규 카카오 회원에게도 가입 쿠폰 자동 지급
		rouletteService.issueSignupCoupon(newMember.getMemberNo());

		return memberMapper.readOneMemberWithRoles(memberId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMemberGradeDirectly(Long memberNo, String grade) throws Exception {
		if (memberNo == null || grade == null)
			return;

		memberMapper.updateGrade(memberNo, grade);
		log.info("[등급 업데이트] 회원번호: {}, 변경된 등급: {}", memberNo, grade);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMemberInfo(Long memberNo, MemberUpdateRequestDTO request) throws Exception {
		if (memberNo == null) {
			throw new IllegalArgumentException("회원 번호가 없습니다.");
		}

		if (request == null) {
			throw new IllegalArgumentException("수정할 정보가 없습니다.");
		}

		// 비밀번호 변경
		if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {

			if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
				throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
			}

			Member existing = memberMapper.readOneMemberByNo(memberNo);
			if (existing == null) {
				throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
			}

			if (!passwordEncoder.matches(request.getCurrentPassword(), existing.getPassword())) {
				throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
			}

			String encodedPassword = passwordEncoder.encode(request.getNewPassword());
			memberMapper.updatePassword(memberNo, encodedPassword);

			log.info("(MemberService) 회원 {}번 비밀번호 변경 완료", memberNo);
			return;
		}

		// 이메일 형식 검사
		if (request.getEmail() != null && !request.getEmail().isBlank()) {
			if (!isValidEmail(request.getEmail().trim())) {
				throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
			}
		}

		Member member = new Member();
		member.setMemberNo(memberNo);

		member.setName(trimToNull(request.getName()));
		member.setNickName(trimToNull(request.getNickName()));
		member.setEmail(trimToNull(request.getEmail()));
		member.setPhoneNumber(trimToNull(request.getPhoneNumber()));

		// gender는 "", "M", "F" 그대로 사용
		member.setGender(request.getGender());
		member.setBirthday(request.getBirthday());

		member.setZipCode(trimToNull(request.getZipCode()));
		member.setBasicAddress(trimToNull(request.getBasicAddress()));
		member.setDetailAddress(trimToNull(request.getDetailAddress()));

		memberMapper.updateMemberInfo(member);

		log.info("(MemberService) 회원 {}번 정보 수정 완료", memberNo);
	}

	private String trimToNull(String value) {
		if (value == null)
			return null;
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private boolean isValidEmail(String email) {
		return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	}

	@Override
	public int checkMemberId(String memberId) throws Exception {
		return memberMapper.checkMemberId(memberId);
	}

	@Override
	public int checkMemberNickName(String nickName) throws Exception {
		return memberMapper.checkNickName(nickName);
	}
}