package com.shop.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {

	private Long logNo; // 이력 번호 (PK, seq_login_history)
	private Long memberNo; // 회원 번호 (FK → member)
	private LocalDateTime loginAt; // 로그인 일시 (DB에서 SYSTIMESTAMP 자동 처리)
	private String ipAddress; // 접속 IP 주소
}