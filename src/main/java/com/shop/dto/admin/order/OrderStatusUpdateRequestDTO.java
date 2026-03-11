package com.shop.dto.admin.order;

import lombok.Data;

//주문 상태 변경 요청 DTO
@Data
public class OrderStatusUpdateRequestDTO {
	private String orderStatus;
}
