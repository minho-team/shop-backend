package com.shop.dto.user.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemCreateRequestDTO {
	private Long productOptionNo;
	private Integer quantity;
	private Long unitPrice;
	private String itemName;
	private String itemSize;
	private String itemColor;
}