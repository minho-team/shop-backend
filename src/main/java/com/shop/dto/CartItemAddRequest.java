package com.shop.dto;

import lombok.Data;

@Data
public class CartItemAddRequest {
	private Long productOptionNo;
	private int quantity;
}
