package com.shop.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
@Component
public class JwtCheckFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// jwt필터는 로그인 중에 api요청에 대해 실행하는 것

		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = null;

		for (Cookie cookie : cookies) {
			if ("accessToken".equals(cookie.getName())) {
				// 쿠키들 하나씩 보고 이름이 액세스토큰인 쿠키가 있으면 밸류 가져오기
				// 이게 가능한 이유는, 서버에서 쿠키 내려줄 때 이름을 accessToken이라고 했기때문
				token = cookie.getValue();
				break;
			}
		}

		if (token == null) {
			// 검사했는데 토큰이 없으면 다음 필터체인으로 넘김
			filterChain.doFilter(request, response);
			return;
		}

		try {
			if (jwtUtil.validateToken(token)) {
				// 토큰에서 유저네임 추출
				String username = jwtUtil.getUsername(token);

				List<SimpleGrantedAuthority> authorities = Arrays.stream(jwtUtil.getRoles(token))
						.map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());

				// 인증 세팅을 위한 토큰 객체 만들기
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
						null, authorities);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			log.info("jwt check error: " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("INVALID_TOKEN");
			return;
		}
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		return path.startsWith("/auth/login") 
				|| path.startsWith("/auth/refresh") 
				|| path.startsWith("/auth/signup");

	}

}