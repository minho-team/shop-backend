package com.shop.dto.admin.order;

import lombok.Data;

//환불 상태 변경 요청 DTO
@Data
public class RefundStatusUpdateRequestDTO {
	private String refundStatus;
}
