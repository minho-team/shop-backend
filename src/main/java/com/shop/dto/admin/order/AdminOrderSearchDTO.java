package com.shop.dto.admin.order;

import lombok.Data;

//주문 목록 검색용 DTO
@Data
public class AdminOrderSearchDTO {
	private Integer page;
    private Integer size;
    private Long orderNo;
    private String ordererName;
    private String orderStatus;
    private String refundStatus;
}
