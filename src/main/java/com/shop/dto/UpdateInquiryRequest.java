package com.shop.dto;

import lombok.Data;

// 게시글 수정 요청 데이터를 담는 DTO
@Data
public class UpdateInquiryRequest {

    // 수정할 카테고리
    private String category;

    // 수정할 제목
    private String title;

    // 수정할 내용
    private String content;

    // 수정할 비밀글 여부 (Y: 비밀글 / N: 공개글)
    private String secretYn;
}
