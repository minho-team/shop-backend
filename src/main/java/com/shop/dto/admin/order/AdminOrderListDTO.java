package com.shop.dto.admin.order;

import java.time.LocalDateTime;

import lombok.Data;

//주문 목록 응답 DTO
@Data
public class AdminOrderListDTO {
	private Long orderNo;
    private Long memberNo;
    private String ordererName;
    private String orderStatus;
    private Integer totalPrice;
    private String refundStatus;
    private LocalDateTime createdAt;
}
