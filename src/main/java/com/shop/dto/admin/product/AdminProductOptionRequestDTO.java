package com.shop.dto.admin.product;

import lombok.Data;

//옵션 등록/수정 DTO
@Data
public class AdminProductOptionRequestDTO {
	private String color;
    private String optionSize;
    private Integer stock;
    private String useYn;
}
