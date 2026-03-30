package com.shop.dto.admin.product;

import lombok.Data;

// 기본정보 수정 DTO
@Data
public class AdminProductBasicUpdateDTO {
	
	private String productName;
    private Long price;
    private Integer discountRate;
    private String useYn;
    private String sameDayDeliveryYn;
    private String description;
}
