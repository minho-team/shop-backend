package com.shop.controller.auth;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.dto.user.auth.RegisterRequestDto;
import com.shop.security.JwtUtil;
import com.shop.security.domain.LoginDto;
import com.shop.service.user.auth.KakaoService;
import com.shop.service.user.member.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final KakaoService kakaoService;

    // =============================================
    // 로그인
    // POST /api/auth/login
    // =============================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto, HttpServletResponse response) {

        try {
            // ★ 핵심 수정: memberId가 null이거나 빈 값이면 즉시 거부
            if (dto.getMemberId() == null || dto.getMemberId().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디를 입력해주세요.");
            }
            if (dto.getPassword() == null || dto.getPassword().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호를 입력해주세요.");
            }

            // Spring Security 인증 처리 (MyUserDetailsService → BCrypt 비밀번호 검증)
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            dto.getMemberId(), dto.getPassword()));

            // JWT 액세스 토큰 (15분) / 리프레시 토큰 (7일) 발급
            String accessToken  = jwtUtil.createToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken(authentication);

            // 인증된 memberId로 회원 정보 조회 후 리프레시 토큰 DB 저장
            Member member = memberService.readOneMember(authentication.getName());
            memberService.updateRefreshToken(member.getMemberId(), refreshToken);

            // 액세스 토큰 쿠키 (httpOnly, 15분)
            ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(60 * 15)
                    .build();

            // 리프레시 토큰 쿠키 (httpOnly, 7일)
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(60 * 60 * 24 * 7)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            log.info("로그인 성공 - memberId: {}", member.getMemberId());
            return ResponseEntity.ok(Map.of("message", "로그인 성공"));

        } catch (Exception e) {
            log.warn("로그인 실패 - 사유: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    // =============================================
    // 액세스 토큰 재발급
    // POST /api/auth/refresh
    // =============================================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 refreshToken 추출
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 없습니다.");
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 없습니다.");
        }

        // JWT 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 만료되었습니다.");
        }

        // 토큰에서 memberId 추출 후 회원 조회
        String memberId = jwtUtil.getMemberId(refreshToken);
        Member member;
        try {
            member = memberService.readOneMemberWithRoles(memberId);
        } catch (Exception e) {
            log.warn("refresh - 회원 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
        }

        // DB에 저장된 refreshToken과 비교 (탈취 방지)
        if (!refreshToken.equals(member.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 일치하지 않습니다.");
        }

        // 새 액세스 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                memberId, null,
                member.getMemberRoleList().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                        .collect(Collectors.toList()));

        String newAccessToken = jwtUtil.createToken(authentication);

        // 새 액세스 토큰 쿠키 (15분)
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 15)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        log.info("액세스 토큰 재발급 완료 - memberId: {}", memberId);
        return ResponseEntity.ok("Access Token 재발급 완료");
    }

    // =============================================
    // 회원가입
    // POST /api/auth/register
    // =============================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto dto) {
        try {
            memberService.register(dto);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패: " + e.getMessage());
        }
    }

    // =============================================
    // 로그아웃
    // POST /api/auth/logout
    // =============================================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, Authentication authentication) {

        // 로그인 상태이면 DB의 refreshToken 초기화
        if (authentication != null) {
            String memberId = authentication.getName();
            try {
                memberService.updateRefreshToken(memberId, "");
                log.info("로그아웃 완료 - memberId: {}", memberId);
            } catch (Exception e) {
                log.warn("리프레시 토큰 초기화 실패: {}", e.getMessage());
            }
        }

        // 쿠키 만료(삭제)
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true).path("/").maxAge(0).build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).path("/").maxAge(0).build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok("로그아웃 완료");
    }

    // =============================================
    // 내 정보 조회 (로그인 상태 확인용)
    // GET /api/auth/me
    // =============================================
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("memberId",   memberId);
            result.put("memberName", member.getName());
            result.put("memberNo",   member.getMemberNo());
            result.put("purchaseCount", member.getPurchaseCount()); 
            result.put("roles", authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .toList());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.warn("/me 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("내 정보 조회 실패");
        }
    }

    // =============================================
    // 카카오 로그인
    // POST /api/auth/kakao
    // =============================================
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> body, HttpServletResponse response) {

        try {
            String code = body.get("code");
            if (code == null || code.isBlank()) {
                return ResponseEntity.badRequest().body("인가코드가 없습니다.");
            }

            // 카카오 인가코드 → 액세스 토큰 → 사용자 정보 조회
            String kakaoAccessToken       = kakaoService.getAccessToken(code);
            Map<String, Object> kakaoUserInfo = kakaoService.getKakaoUserInfo(kakaoAccessToken);

            // 카카오 회원 조회 or 신규 생성
            Member member = memberService.getOrCreateKakaoMember(kakaoUserInfo);

            // JWT 발급
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    member.getMemberId(), null,
                    member.getMemberRoleList().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                            .collect(Collectors.toList()));

            String accessToken  = jwtUtil.createToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken(authentication);
            memberService.updateRefreshToken(member.getMemberId(), refreshToken);

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true).secure(false).path("/").maxAge(60 * 15).build();
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true).secure(false).path("/").maxAge(60 * 60 * 24 * 7).build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            log.info("카카오 로그인 성공 - memberId: {}", member.getMemberId());
            return ResponseEntity.ok(Map.of("message", "카카오 로그인 성공"));

        } catch (Exception e) {
            log.warn("카카오 로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("카카오 로그인 실패: " + e.getMessage());
        }
    }
}