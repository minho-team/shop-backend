package com.shop.dto.user.product;

import java.util.List;

import com.shop.domain.Product;
import com.shop.domain.ProductOption;

import lombok.Data;

@Data
public class ProductDetailResponse {
	private Product product;
	private List<ProductOption> options;
}
