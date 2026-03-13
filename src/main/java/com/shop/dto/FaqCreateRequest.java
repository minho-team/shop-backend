package com.shop.dto;

import lombok.Data;

// FAQ 등록 요청 DTO
// 관리자가 FAQ를 등록할 때 전송하는 데이터
@Data
public class FaqCreateRequest {

    // FAQ 번호 (시퀀스 채번 후 MyBatis가 자동 주입 - selectKey용)
    private Long faqNo;

    // 카테고리 (배송 / 주문/결제 / 취소/교환/반품 / 상품/AS문의 / 회원정보 / 서비스 / 이용안내)
    private String category;

    // 질문 내용
    private String question;

    // 답변 내용
    private String answer;

    // 정렬 순서 (숫자가 작을수록 먼저 표시)
    private Integer sortOrder;
}