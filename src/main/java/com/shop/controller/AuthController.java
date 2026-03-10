package com.shop.controller;

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
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.dto.RegisterRequestDto;
import com.shop.security.JwtUtil;
import com.shop.security.domain.LoginDto;
import com.shop.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final MemberService memberService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDto dto, HttpServletResponse response) {

		try {

			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(dto.getMemberId(), dto.getPassword()));

			String accessToken = jwtUtil.createToken(authentication);
			String refreshToken = jwtUtil.createRefreshToken(authentication);

			// principal의 username => userId를 의미
			// 인증된 유저의 userId를 기반으로 그 정보를 가져옴, 조인은 비용이 크기에 쿼리를 따로 하나 만듦(권한은 받아오지 않을거임)
			log.info("auth contorlelr:" + authentication.getName());
			Member member = memberService.readOneMember(authentication.getName());
			memberService.updateRefreshToken(member.getMemberId(), refreshToken);

			ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken).secure(false).path("/")
					.httpOnly(true).maxAge(60 * 30).build();

			ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken).secure(false)
					.httpOnly(true).maxAge(60 * 60 * 24 * 7).path("/").build();

			response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
			response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

			return ResponseEntity.ok(Map.of("message", "로그인성공"));

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패" + e.getMessage());
		}

	}

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

		// JWT 자체 유효성 검증
		if (!jwtUtil.validateToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 유효하지 않습니다.");
		}

		// 토큰에서 userId 추출
		String userId = jwtUtil.getUserId(refreshToken);

		// userId를 기반으로 Member 정보 받아오기
		Member member = null;
		try {
			member = memberService.readOneMemberWithRoles(userId);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("authcontroller에서 refresh토큰 함수 에러");
		}

		// DB에 저장된 refreshToken과 비교
		if (!refreshToken.equals(member.getRefreshToken())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 일치하지 않습니다.");
		}

		// 새 Access Token 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null,
				member.getMemberRoleList().stream()
						.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
						.collect(Collectors.toList()));

		String newAccessToken = jwtUtil.createToken(authentication);

		// 새 Access Token을 쿠키로 내려줌
		ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken).httpOnly(true).secure(false) // 일단
																														// //
																														// 쿠키보냄)
				.path("/").maxAge(60 * 30) // 30분
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

		return ResponseEntity.ok().body("Access Token 재발급 완료");
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDto dto) {
		
		try {
			memberService.register(dto);
			return ResponseEntity.ok().body("회원가입 성공");
		} catch (Exception e) {
			log.info("로그인 실패 에러메시지:" +e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 실패");
		}
		
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response, Authentication authentication) {

		// DB에서 refreshToken 초기화
		if (authentication != null) { // 로그인 상태인지 확인
			String userId = authentication.getName(); // detailService에서 세팅된 userId

			Member member = null;

			try {
				member = memberService.readOneMember(userId);
				log.info("로그아웃 때 받아온 member의 이메일 확인:" + member.getEmail());
				// 리프레시토큰을 무효화
				memberService.updateRefreshToken(userId, "");
			} catch (Exception e) {
				e.printStackTrace();
				log.info("리프레시토큰 무효화 과정에서 에러");
			}

		}
		// accessToken 쿠키 삭제
		ResponseCookie accessCookie = ResponseCookie.from("accessToken", "").httpOnly(true).path("/").maxAge(0) // 0으로
																												// // 삭제
				.build();

		// refreshToken 쿠키 삭제
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "").httpOnly(true).path("/").maxAge(0)
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		return ResponseEntity.ok("로그아웃 완료");
	}
	
	@GetMapping("/me")
	public ResponseEntity<?> me(Authentication authentication) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ResponseEntity.status(401).body("Unauthorized, 로그인 안 되어 있는 상태");
	    }

	    String memberId = authentication.getName();

	    Map<String, Object> result = Map.of(
	        "memberId", memberId, //이게 userId임
	        "roles", authentication.getAuthorities().stream()
	                .map(a -> a.getAuthority())
	                .toList()
	    );
	    log.info("멤버의 권한 리스트:"+ result.get("roles"));

	    return ResponseEntity.ok(result);
	}
	

}
