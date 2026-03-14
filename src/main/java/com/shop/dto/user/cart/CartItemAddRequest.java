package com.shop.dto.user.cart;

import lombok.Data;

@Data
public class CartItemAddRequest {
	private Long productOptionNo;
	private int quantity;
}
