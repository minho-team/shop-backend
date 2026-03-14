package com.shop.dto.user.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageListDTO {

	private Long productNo;
	private List<ProductImageDTO> images;
}
