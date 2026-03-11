package com.shop.dto.admin.order;

import lombok.Data;

//주문 상품 DTO
@Data
public class AdminOrderItemDTO {
	private Long orderItemNo;
    private Long productOptionNo;
    private String itemName;
    private String itemSize;
    private String itemColor;
    private Integer quantity;
    private Integer unitPrice;
}
