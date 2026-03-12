package com.shop.domain;

import lombok.Data;

@Data
public class ProductImage {
	private Long productImgNo;
	private Long productNo;
	private String imageUrl;
	private String imageType;
	private Integer sortOrder;
}
