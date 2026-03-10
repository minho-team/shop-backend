package com.shop.mapper;

import com.shop.domain.Member;
import com.shop.domain.MemberRole;

public interface MemberMapper {
	
	Member readOneMemberWithRoles(String memberId) throws Exception;
	
	Member readOneMember(String readOneMember) throws Exception;
	
	void updateRefreshToken(String memberId, String refreshToken) throws Exception;

	void register(Member member) throws Exception;

	void insertRole(MemberRole role) throws Exception;

	void insertAdmin(Member member) throws Exception;

	void insertAdminRole(MemberRole mr)throws Exception;
	
}
