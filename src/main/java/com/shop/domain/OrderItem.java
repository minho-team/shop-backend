package com.shop.domain;

import lombok.Data;

@Data
public class OrderItem {
    private Long orderItemNo;        // 주문항목번호
    private Long orderNo;            // 주문번호
    private Long productNo;          // 상품번호
    private Long productOptionNo;    // 상품옵션번호
    private Integer quantity;        // 수량
    private Integer unitPrice;       // 단가
    private String itemName;         // 상품명
    private String itemSize;         // 상품사이즈
    private String itemColor;        // 상품색상
    private String imageUrl;         // 상품이미지
}