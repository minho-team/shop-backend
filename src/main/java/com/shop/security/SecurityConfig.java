package com.shop.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtCheckFilter jwtCheckFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .csrf(csrf -> csrf.disable())
	        .formLogin(form -> form.disable())
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/api/auth/**").permitAll()
	            // [수정] 변경 전: .requestMatchers("/api/inquiry/**").permitAll()
	            // 전체 permitAll 시 JWT 인증 정보가 null로 주입되는 문제 → 메서드별로 분리
	            .requestMatchers(HttpMethod.GET, "/api/inquiry/**").permitAll()         // 조회는 비로그인도 허용
	            .requestMatchers(HttpMethod.POST, "/api/inquiry/**").authenticated()    // 등록은 로그인 필수
	            .requestMatchers(HttpMethod.PUT, "/api/inquiry/**").authenticated()     // 수정은 로그인 필수
	            .requestMatchers(HttpMethod.DELETE, "/api/inquiry/**").authenticated()  // 삭제는 로그인 필수
	            // [수정 끝]
	            .requestMatchers("/api/faq/**").permitAll()
	            .requestMatchers("/upload/**").permitAll()
	            // 상품 조회는 모두 허용
	            .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
	            // 상품 등록/수정/삭제는 관리자만
	            .requestMatchers(HttpMethod.POST, "/api/product/**").hasRole("ADMIN")
	            .requestMatchers(HttpMethod.PUT, "/api/product/**").hasRole("ADMIN")
	            .requestMatchers(HttpMethod.DELETE, "/api/product/**").hasRole("ADMIN")
	            .requestMatchers("/admin/**").hasRole("ADMIN")
	            .requestMatchers("/api/order/**", "/api/cart/**", "/api/cart/item/**").hasRole("USER")
	            .anyRequest().authenticated()
	        );
	    http.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);
	    return http.build();
	}

	@Bean
	AuthenticationManager createAuthManager(AuthenticationConfiguration configuration) {
		return configuration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder createPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173"));
		config.setAllowedMethods(List.of("PUT", "PATCH", "POST", "DELETE", "GET"));
		config.setAllowedHeaders(List.of("Authorization", "Content-type", "Cookie"));
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}