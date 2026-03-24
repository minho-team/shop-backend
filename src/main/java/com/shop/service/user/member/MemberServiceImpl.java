package com.shop.service.user.member;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Member;
import com.shop.domain.MemberRole;
import com.shop.dto.user.auth.RegisterRequestDto;
import com.shop.mapper.user.MemberMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Member readOneMemberWithRoles(String memberId) throws Exception {
		return memberMapper.readOneMemberWithRoles(memberId);
	}

	@Override
	public Member readOneMember(String memberId) throws Exception {
		return memberMapper.readOneMember(memberId);
	}

	@Override
	@Transactional
	public void updateRefreshToken(String memberId, String refreshToken) throws Exception {
		memberMapper.updateRefreshToken(memberId, refreshToken);
	}

	@Override
	@Transactional
	public void register(RegisterRequestDto dto) throws Exception {
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

		memberMapper.register(member);

		MemberRole role = new MemberRole();
		role.setRoleName("USER");
		role.setMemberNo(member.getMemberNo());

		memberMapper.insertRole(role);
	}

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

	@Override
	public Member getOrCreateKakaoMember(Map<String, Object> kakaoUserInfo) throws Exception {
		Object idObj = kakaoUserInfo.get("id");
		if (idObj == null) {
			throw new RuntimeException("카카오 사용자 id가 없습니다.");
		}

		String kakaoId = String.valueOf(idObj);
		String memberId = "k_" + kakaoId;

		Member existingMember = null;
		try {
			existingMember = memberMapper.readOneMember(memberId);
		} catch (Exception e) {
		}

		if (existingMember != null) {
			return memberMapper.readOneMemberWithRoles(memberId);
		}

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

	// 구매횟수 관련 로직

	@Override
	@Transactional(rollbackFor = Exception.class) 
	public void updateMemberGrade(Long memberNo) throws Exception {
	    if (memberNo == null) return;
	    int currentCount = memberMapper.getPurchaseCount(memberNo);
	    
	    String grade = determineGrade(currentCount);
	    
	    log.info("(MemberServiceImpl) 회원 {}번의 현재 구매 횟수는 {}회이며, 판정된 등급은 {}입니다.", 
	              memberNo, currentCount, grade);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void increasePurchaseCount(Long memberNo) throws Exception {
		if (memberNo == null)
			return;

		memberMapper.incrementPurchaseCount(memberNo);
		log.info("(MemberServiceImpl) 회원 {}번 구매 횟수 1 증가 완료", memberNo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void decreasePurchaseCount(Long memberNo) throws Exception {
		if (memberNo == null)
			return;
		memberMapper.decrementPurchaseCount(memberNo);
		log.info("(MemberServiceImpl) 회원 {}번 구매 횟수 1 차감 완료", memberNo);
	}

	public String determineGrade(int count) {
		if (count >= 30)
			return "VVIP";
		if (count >= 20)
			return "VIP";
		if (count >= 10)
			return "GOLD";
		if (count >= 5)
			return "SILVER";
		return "BASIC";
	}
}