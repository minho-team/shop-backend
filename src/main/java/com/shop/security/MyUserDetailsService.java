package com.shop.security;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shop.domain.Member;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;


@RequiredArgsConstructor
@Service
@Log
public class MyUserDetailsService implements UserDetailsService{

	private final MemberService memberService;
	
	
	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
		Member member;
		try {
			member = memberService.readOneMemberWithRoles(memberId);
			log.info("detailService, memberId:"+memberId);

			List<GrantedAuthority> authorities =
					member.getMemberRoleList().stream()
					.map((role)->new SimpleGrantedAuthority("ROLE_"+role.getRoleName()))
					.collect(Collectors.toList());
			log.info("detailService, authorities:"+authorities);	
			
			return User.builder() //이 세개가 Authentication의 principal로 들어갈 객체
					.username(member.getMemberId()) 
					.password(member.getPassword()) 
					.authorities(authorities)
					.build();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberId);
		}
			
	}

}