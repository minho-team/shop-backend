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
public class MemberMemo {

	private Long memoNo; // 메모 번호 (PK, seq_member_memo)
	private Long memberNo; // 회원 번호 (FK → member)
	private String content; // 메모 내용
	private LocalDateTime createdAt; // 작성 일시 (DB에서 SYSTIMESTAMP 자동 처리)
}