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
public class Point {

	private Long pointNo; // 포인트 번호 (PK, seq_point)
	private Long memberNo; // 회원 번호 (FK → member)
	private int point; // 포인트 (양수=적립, 음수=차감)
	private String type; // EARN(적립) / USE(사용) / EXPIRE(만료) / ADMIN(관리자조정)
	private LocalDateTime createdAt; // 처리 일시
}