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
public class MyUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    // =============================================
    // Spring Security 인증 처리 메서드
    // - AuthController에서 authenticationManager.authenticate() 호출 시 자동 실행
    // - memberId로 회원 + 권한 목록 조회 후 UserDetails 객체 반환
    // =============================================
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        // ★ 핵심 수정: memberId가 null이거나 빈 값이면 즉시 예외 처리
        // 프론트에서 아이디 필드를 비워서 보내거나 필드명이 잘못된 경우 방어
        if (memberId == null || memberId.isBlank()) {
            throw new UsernameNotFoundException("아이디가 입력되지 않았습니다.");
        }

        log.info("detailService, memberId:" + memberId);

     // DB에서 회원 + 권한 목록 함께 조회
        Member member;
        try {
            member = memberService.readOneMemberWithRoles(memberId);
        } catch (Exception e) {
            throw new UsernameNotFoundException("회원 조회 중 오류 발생: " + memberId);
        }

        // 조회 결과가 없으면 예외 처리
        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다: " + memberId);
        }

        // 권한 목록이 없으면 예외 처리
        if (member.getMemberRoleList() == null || member.getMemberRoleList().isEmpty()) {
            throw new UsernameNotFoundException("권한 정보가 없는 계정입니다: " + memberId);
        }

        // member_role 목록이 없으면 예외 처리 (권한 데이터 누락)
        if (member.getMemberRoleList() == null || member.getMemberRoleList().isEmpty()) {
            throw new UsernameNotFoundException("권한 정보가 없는 계정입니다: " + memberId);
        }

        // member_role 목록을 Spring Security가 인식하는 GrantedAuthority 목록으로 변환
        // ex) "USER" → "ROLE_USER", "ADMIN" → "ROLE_ADMIN"
        List<GrantedAuthority> authorities = member.getMemberRoleList().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());

        log.info("detailService, authorities:" + authorities);

        // Spring Security UserDetails 객체 반환
        // - username: memberId (Authentication.getName()으로 꺼낼 수 있음)
        // - password: BCrypt 암호화된 비밀번호 (자동으로 검증됨)
        // - authorities: 권한 목록
        return User.builder()
                .username(member.getMemberId())
                .password(member.getPassword())
                .authorities(authorities)
                .build();
    }
}