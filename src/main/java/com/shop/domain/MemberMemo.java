package com.shop.domain;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberMemo {
    private Long memoNo;             // 메모 고유 번호
    private Long memberNo;           // 회원 번호
    private String content;          // 메모 내용
    private LocalDateTime createdAt; // 작성 일시
}