package com.shop.dto.admin.product;

import lombok.Data;

//상품 검색 DTO
@Data
public class AdminProductSearchDTO {
	private Long productNo;
    private String name;
    private Long categoryId;
    private String useYn;
    private String sameDayDeliveryYn;
}
