package com.shop.dto.admin.product;

import lombok.Data;

//상품 이미지 DTO
@Data
public class AdminProductImageDTO {
	
	private Long productImgNo;
    private Long productNo;
    private String imageUrl;
    private String imageType;
    private Integer sortOrder;
}
