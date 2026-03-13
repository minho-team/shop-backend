package com.shop.dto.admin.product;

import java.time.LocalDateTime;

import lombok.Data;

//상품 목록 DTO
@Data
public class AdminProductListDTO {
	private Long productNo;
    private String name;
    private Integer price;
    private Integer salePrice;
    private Long categoryId;
    private String useYn;
    private LocalDateTime createdAt;
    private Integer viewCount;
    private String sameDayDeliveryYn;
}
