package com.shop.dto.admin.product;

import java.time.LocalDateTime;

import lombok.Data;

//상품 목록 DTO
@Data
public class AdminProductListDTO {
	private Long productNo;
    private String thumbnailUrl;
    private String name;
    private String categoryName;
    private int price;
    private int discountRate;
    private int salePrice;
    private String useYn;
    private int viewCount;
    private String sameDayDeliveryYn;
    private LocalDateTime createdAt;
}
