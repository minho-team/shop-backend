package com.shop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shop.domain.Member;
import com.shop.service.MemberService;

import lombok.RequiredArgsConstructor;

@SpringBootTest
class ShopApplicationTests {

	@Autowired
	private MemberService memberService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void insertTestAdmin() throws Exception {
		Member member = new Member();
		member.setMemberId("admin");
		member.setPassword(passwordEncoder.encode("admin"));
		member.setName("관리자");
		member.setNickName("adminNick");
		member.setEmail("admin@test.com");
		member.setPhoneNumber("010-1111-2222");

		memberService.insertMember(member);
	}

}
