package com.shop.dto.admin.product;

import java.time.LocalDateTime;

import lombok.Data;

// 상품 기본 상세 DTO
@Data
public class AdminProductDetailDTO {
	private Long productNo;
    private String name;
    private Long price;
    private Integer discountRate;
    private Long salePrice;
    private Long categoryId;
    private String description;
    private String useYn;
    private String sameDayDeliveryYn;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
