package com.shop.service.Impl;

import org.springframework.stereotype.Service;

import com.shop.domain.Member;
import com.shop.mapper.MemberMapper;
import com.shop.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberMapper memberMapper;

	// 유저+권한과 함께 조인해서 가져오는 쿼리
	@Override
	public Member readOneMemberWithRoles(String memberId) throws Exception {
		return memberMapper.readOneMemberWithRoles(memberId);
	}

	// 유저테이블만 접근해서 멤버의 정보를 가져오는 쿼리
	@Override
	public Member readOneMember(String memberId) throws Exception {
		return memberMapper.readOneMember(memberId);
	}

	@Override
	public void updateRefreshToken(String memberId, String refreshToken) throws Exception {
		memberMapper.updateRefreshToken(memberId,refreshToken);
	}

	@Override
	public void insertMember(Member member) throws Exception {
		memberMapper.insertMember(member);
		
	}


}
