package com.shop.dto.admin.product;

import lombok.Data;

// 옵션 응답 DTO
@Data
public class AdminProductOptionDTO {
	
	private Long productOptionNo;
    private Long productNo;
    
    private String optionSize;
    private String color;
    private Integer stock;
    private String useYn;
}
