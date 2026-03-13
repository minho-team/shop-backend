package com.shop.dto;

import lombok.Data;

// 1:1 문의 게시글 작성 요청 데이터를 담는 DTO
@Data
public class InquiryCreateRequest {

    // 문의 번호 (INSERT 전 selectKey로 시퀀스 값 자동 주입)
    private Long inquiryNo;

    // 작성자 회원 번호 (JWT에서 추출하여 컨트롤러에서 주입)
    private Long memberNo;

    // 문의 카테고리 (배송 / 주문/결제 / 취소/교환/반품 / 상품/AS문의 / 회원정보 / 서비스 / 이용안내)
    private String category;

    // 게시글 제목
    private String title;

    // 게시글 내용
    private String content;

    // 비밀글 여부 (Y: 비밀글 / N: 공개글), 기본값 N
    private String secretYn = "N";
}