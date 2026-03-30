package com.shop.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Wishlist {
	private Long wishlistNo;
	private Long memberNo;
	private Long productNo;
	private LocalDateTime createdAt;
}
