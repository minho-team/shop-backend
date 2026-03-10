package com.shop.security;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	private final String SECRET_KEY="domino-my-super-key-12345160-domino-24843343";
	
	private final long EXPIRATION_TIME =3600000;
	private final long REFRESH_EXPIRATION_TIME = 604800000;
	
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}
	
	//토큰생성
	public String createToken(Authentication authentication) {
		
		String username = authentication.getName();
		String roles = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		Date now = new Date();
		Date expiryDate = new Date(now.getTime()+EXPIRATION_TIME);
		
		return Jwts.builder()
				.setSubject(username)
				.claim("roles",roles)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(getSigningKey(),SignatureAlgorithm.HS256)
				.compact();
		
	}
	
	
	public String createRefreshToken(Authentication authentication) {
		
		String userId = authentication.getName();
		
		Date now = new Date();
		Date expiryDate = new Date(now.getTime()+REFRESH_EXPIRATION_TIME);
		
		return Jwts.builder()
				.setSubject(userId)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(getSigningKey(),SignatureAlgorithm.HS256)
				.compact();	
	}
	
	//토큰에서 이름 추출
	public String getUserId(String token) {
		return  Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
	}
	
	// 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    // 토큰에서 권한 뽑아내기
    public String[] getRoles(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", String.class)
                .split(",");
    }
	
	
	
	
	
}
