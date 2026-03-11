package com.shop.domain;

import lombok.Data;
import java.sql.Timestamp;

// FAQ 테이블 데이터를 담는 도메인 클래스
@Data
public class Faq {

    // FAQ 번호 (PK)
    private Long faqNo;

    // 카테고리 (배송 / 주문/결제 / 취소/교환/반품 / 상품/AS문의 / 회원정보 / 서비스 / 이용안내)
    private String category;

    // 질문
    private String question;

    // 답변
    private String answer;

    // 정렬 순서
    private Integer sortOrder;

    // 삭제 여부 (N: 정상 / Y: 삭제)
    private String deleteYn;

    // 등록일
    private Timestamp createdAt;
}
