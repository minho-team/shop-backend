package com.shop.service.Impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Member;
import com.shop.domain.MemberRole;
import com.shop.dto.RegisterRequestDto;
import com.shop.mapper.MemberMapper;
import com.shop.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;

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

	//로그인 시 리프레시토큰 갱신
	@Override
	@Transactional
	public void updateRefreshToken(String memberId, String refreshToken) throws Exception {
		memberMapper.updateRefreshToken(memberId,refreshToken);
	}

	//회원가입
	@Override
	@Transactional
	public void register(RegisterRequestDto dto) throws Exception {
		//dto를 entity로
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

	//관리자 계정 넣어놓는 함수
	
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


}
