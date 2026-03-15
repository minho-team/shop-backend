package com.shop.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CartItem {
	private Long cartItemNo;
	private Long cartNo;
	private Long productOptionNo;
	private Integer quantity;
	private LocalDateTime createdAt;
}
