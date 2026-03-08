package com.shop.mapper;

import com.shop.domain.Member;

public interface MemberMapper {
	
	Member readOneMemberWithRoles(String memberId) throws Exception;
	
	Member readOneMember(String readOneMember) throws Exception;
	
	void updateRefreshToken(String memberId, String refreshToken) throws Exception;

	void insertMember(Member member) throws Exception;
	
}
