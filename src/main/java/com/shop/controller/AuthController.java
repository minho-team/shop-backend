package com.shop.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.security.JwtUtil;
import com.shop.security.domain.LoginDto;
import com.shop.service.MemberService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@RestController
@RequestMapping("/auth")
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
					.authenticate(new UsernamePasswordAuthenticationToken(dto.getUserId(), dto.getPassword()));

			String accessToken = jwtUtil.createToken(authentication);
			String refreshToken = jwtUtil.createRefreshToken(authentication);
			
			//principal의 username => userId를 의미
			//인증된 유저의 userId를 기반으로 그 정보를 가져옴, 조인은 비용이 크기에 쿼리를 따로 하나 만듦(권한은 받아오지 않을거임)
			log.info("auth contorlelr:"+authentication.getName());
			Member member = memberService.readOneMember(authentication.getName());
			memberService.updateRefreshToken(member.getMemberId(),refreshToken);

			ResponseCookie accessCookie = ResponseCookie
					.from("accessToken", accessToken)
					.secure(false)
					.path("/")
					.httpOnly(true)
					.maxAge(60 * 30)
					.build();

			ResponseCookie refreshCookie = ResponseCookie
					.from("refreshToken", refreshToken)
					.secure(false)
					.httpOnly(true)
					.maxAge(60 * 60 * 24 * 7)
					.path("/")
					.build();

			response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
			response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

			return ResponseEntity.ok(Map.of("message", "로그인성공"));

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패"+e.getMessage());
		}

	}
}
