package com.shop.dto.admin.order;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminOrderItemDetailResponseDTO {
	private Long orderItemNo;
	private String itemName;
	private Integer quantity;
	private Long unitPrice;
	private String orderItemStatus;

	private String imageUrl;

	private Long productOptionNo;
	private String optionSize;
	private String optionColor;

	private String ordererName;
	private LocalDateTime createdAt;
	private Long totalPrice;
}
