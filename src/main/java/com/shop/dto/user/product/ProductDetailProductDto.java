package com.shop.dto.user.product;

import lombok.Data;

@Data
public class ProductDetailProductDto {
	private Long productNo;
	private String name;
	private Long price;
	private Long salePrice;
	private Integer discountRate;
	private Long categoryId;
	private String description;
	private String useYn;
	private Integer viewCount;
	private String sameDayDeliveryYn;
}
