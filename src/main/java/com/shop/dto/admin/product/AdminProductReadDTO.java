package com.shop.dto.admin.product;

import java.time.LocalDateTime;

import lombok.Data;

//상품 상세 DTO
@Data
public class AdminProductReadDTO {
	private Long productNo;
    private String name;
    private Integer price;
    private Integer salePrice;
    private Long categoryId;
    private String description;
    private String useYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private String sameDayDeliveryYn;
}
