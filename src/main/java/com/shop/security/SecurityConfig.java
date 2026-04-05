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
	        		
	        		//사용자
	        		.requestMatchers("/api/auth/**").permitAll()
	        		.requestMatchers("/api/cart/**").hasRole("USER")
	        		.requestMatchers("/api/cart/item/**").hasRole("USER")
	        		.requestMatchers("/api/category/**").permitAll()
	        		.requestMatchers("/api/comment/**").hasRole("USER")
	        		.requestMatchers("/api/faq/**").permitAll()
	            .requestMatchers("/api/inquiry/**").hasRole("USER")
	            .requestMatchers("/api/member/**").permitAll()
	            .requestMatchers("/api/orders/**").hasRole("USER")
	            .requestMatchers("/api/orders/item/**").hasRole("USER")
	            .requestMatchers("/api/payment/**").hasRole("USER")
	            .requestMatchers("/api/product/**").permitAll()
	            .requestMatchers("/api/product/image/**").permitAll()
	            .requestMatchers("/api/product/option/**").permitAll()
	            .requestMatchers("/api/refund/**").hasRole("USER")
	            .requestMatchers("/api/reviews/**").hasRole("USER")
	            .requestMatchers("/api/roulette/**").hasRole("USER")
	            .requestMatchers("/api/wishlist/**").hasRole("USER")
	            
	            .requestMatchers("/upload/**").permitAll()
	            
	            // 관리자.hasRole("ADMIN") authenticated()
	            .requestMatchers("/api/admin/**").hasRole("ADMIN")
	            .anyRequest().authenticated()
	        );
	    http.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);
	    return http.build();
	}

	@Bean
	AuthenticationManager createAuthManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder createPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173",
				"https://shop-frontend-topaz.vercel.app",
				"https://www.khshop.best",
				"https://khshop.best"));
		config.setAllowedMethods(List.of("PUT", "PATCH", "POST", "DELETE", "GET"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}