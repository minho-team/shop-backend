package com.shop.dto.admin.member;

import lombok.Data;

// ================================================
// 관리자 회원 상세 - 장바구니 아이템 표시용 DTO
// cart_item → product_option → product JOIN 결과
// ================================================
@Data
public class AdminCartItemDTO {

    // 장바구니 아이템 번호
    private Long cartItemNo;

    // 상품 번호
    private Long productNo;

    // 상품명
    private String productName;

    // 옵션 사이즈
    private String optionSize;

    // 옵션 색상
    private String color;

    // 수량
    private Integer quantity;

    // 정가
    private Integer price;

    // 할인가 (null이면 정가 표시)
    private Integer salePrice;
}