package com.shop.dto;

import lombok.Data;

// 게시글 수정 요청 데이터를 담는 DTO
@Data
public class UpdateBoardRequest {

    // 수정할 게시글 제목
    private String title;

    // 수정할 게시글 내용
    private String content;
}