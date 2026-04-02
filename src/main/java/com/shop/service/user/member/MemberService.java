package com.shop.service.user.member;

import java.util.Map;

import com.shop.domain.Member;
import com.shop.dto.user.auth.RegisterRequestDto;
import com.shop.dto.user.member.MemberUpdateRequestDTO;

public interface MemberService {

	public Member readOneMemberWithRoles(String memberId) throws Exception;

	public Member readOneMember(String memberId) throws Exception;

	public void updateRefreshToken(String memberId, String refreshToken) throws Exception;

	public void register(RegisterRequestDto dto) throws Exception;

	public void insertAdmin(Member member) throws Exception;

	Member getOrCreateKakaoMember(Map<String, Object> kakaoUserInfo) throws Exception;

	void updateMemberGrade(Long memberNo) throws Exception;

	void increasePurchaseCount(Long memberNo) throws Exception;

	void decreasePurchaseCount(Long memberNo) throws Exception;

	void updateMemberInfo(Long memberNo, MemberUpdateRequestDTO request) throws Exception;

	// id 중복 체크
	public int checkMemberId(String memberId) throws Exception;

	// 닉네임 중복 체크
	public int checkMemberNickName(String nickName) throws Exception;
}
