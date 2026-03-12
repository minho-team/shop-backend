package com.shop.dto;

import lombok.Data;

@Data
public class ProductListResponse {
    private Long productNo;
    private String name;
    private Long price;
    private Long salePrice;
    private String imageUrl;   // 썸네일 이미지
}
