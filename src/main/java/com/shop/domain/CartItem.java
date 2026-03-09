package com.shop.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class CartItem {
	private int cartItemNo;
	private int cartNo;
	private int productOptionNo;
	private int quantity;
	private Timestamp createdAt;
}
