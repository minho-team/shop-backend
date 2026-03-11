package com.shop.dto;

import lombok.Data;

// 관리자 답변 작성 요청 데이터를 담는 DTO
@Data
public class CommentCreateRequest {

    // 게시글 번호
    private Long boardNo;

    // 작성자 회원 번호 (관리자, JWT에서 추출하여 컨트롤러에서 주입)
    private Long memberNo;

    // 답변 내용
    private String content;
}
