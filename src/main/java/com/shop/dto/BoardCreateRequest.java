package com.shop.dto;

import lombok.Data;

// 게시글 작성 요청 데이터를 담는 DTO
@Data
public class BoardCreateRequest {

    // 게시글 제목
    private String title;

    // 게시글 내용
    private String content;

    // 작성자 회원 번호
    private Long memberNo;
}