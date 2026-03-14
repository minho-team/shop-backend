package com.shop.dto.user.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {

	private Long productImgNo;
	private Long productNo;
	private String imageUrl;
	private String imageType;
	private Integer sortOrder;
}
