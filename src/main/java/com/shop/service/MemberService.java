package com.shop.service;

import com.shop.domain.Member;
import com.shop.dto.RegisterRequestDto;

public interface MemberService {

	public void insertMember(Member member) throws Exception;

	public Member readOneMemberWithRoles(String memberId) throws Exception;

	public Member readOneMember(String memberId) throws Exception;

	public void updateRefreshToken(String memberId, String refreshToken) throws Exception;

	public void register(RegisterRequestDto dto) throws Exception;

}
