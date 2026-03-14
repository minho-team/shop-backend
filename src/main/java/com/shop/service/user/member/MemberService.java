package com.shop.service.user.member;

import com.shop.domain.Member;
import com.shop.dto.user.auth.RegisterRequestDto;

public interface MemberService {

	public Member readOneMemberWithRoles(String memberId) throws Exception;

	public Member readOneMember(String memberId) throws Exception;

	public void updateRefreshToken(String memberId, String refreshToken) throws Exception;

	public void register(RegisterRequestDto dto) throws Exception;
	
	public void insertAdmin(Member member) throws Exception;

}
