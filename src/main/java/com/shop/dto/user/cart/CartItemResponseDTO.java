package com.shop.dto.user.cart;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CartItemResponseDTO {
	private Long cartItemNo;
	private Long cartNo;
	private Long productOptionNo;
	private Integer quantity;
	private LocalDateTime createdAt;

	private String productName;
	private String color;
	private String sizeName;
	private Integer price;
	private Integer discountRate;
	private Long salePrice;

	// 이미지 테이블 조인 붙일 때 사용
	private String imageUrl;
}