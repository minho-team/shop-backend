package com.shop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shop.domain.Member;
import com.shop.domain.MemberRole;
import com.shop.service.user.member.MemberService;

@SpringBootTest
class ShopApplicationTests {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =============================================
    // BCrypt 해시값 생성용 테스트
    // - 실행 후 콘솔에 출력된 $2a$10$... 값을 DB에 UPDATE
    // =============================================
    @Test
    void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin");
        System.out.println("==================================");
        System.out.println("해시값: " + hash);
        System.out.println("==================================");
    }

    // =============================================
    // 테스트 admin 계정 삽입 (필요 시 사용)
    // =============================================
    @Test
    void insertTestAdmin() throws Exception {
        Member member = new Member();
        member.setMemberNo(999999L);
        member.setMemberId("admin");
        member.setPassword(passwordEncoder.encode("admin"));
        member.setName("관리자");
        member.setNickName("adminNick");
        member.setEmail("admin@test.com");
        member.setPhoneNumber("010-1111-2222");

        memberService.insertAdmin(member);
    }

}