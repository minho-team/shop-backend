package com.shop.dto.user.wishlist;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WishlistItemResponseDTO {
	private Long wishlistNo;
	private Long productNo;
	private String name;
	private Long price;
	private Long salePrice;
	private Integer discountRate;
	private String imageUrl;
	private String sameDayDeliveryYn;
	private LocalDateTime wishedAt;
}
