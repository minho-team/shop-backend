package com.shop.dto.admin.member;

import java.time.LocalDateTime;
import lombok.Data;

// 관리자 회원 상세 페이지 - 주문 목록 전용 DTO
// Orders 도메인 대신 이 DTO를 사용하여 공유 도메인 오염을 방지
@Data
public class AdminOrderSummaryDTO {

    private Long orderNo;
    private Long memberNo;
    private String ordererName;
    private String orderStatus;
    private Long totalPrice;
    private LocalDateTime createdAt;

    // 상세 펼침용 필드
    private String receiverName;
    private String receiverPhoneNumber;
    private String receiverZipCode;
    private String receiverBaseAddress;
    private String receiverDetailAddress;
    private String message;

    // 주문 상품 요약 (서브쿼리로 채워짐 - order_item 테이블 JOIN 없이 단순 조회)
    private String firstItemName; // 첫 번째 상품명 (예: "나이키 에어맥스 외 2건")
    private int itemCount;        // 주문에 포함된 상품 총 수
}
