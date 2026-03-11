package com.shop.dto.admin.order;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

//주문 상세 DTO
@Data
public class AdminOrderReadDTO {
	private Long orderNo;
    private Long memberNo;
    private String ordererName;
    private String ordererPhoneNumber;
    private String ordererEmail;
    private String orderStatus;
    private Integer totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    private String receiverName;
    private String receiverPhoneNumber;
    private String receiverZipCode;
    private String receiverBaseAddress;
    private String receiverDetailAddress;
    private String message;

    private String refundStatus;
    private LocalDateTime refundRequestedAt;
    private LocalDateTime refundedAt;

    private List<AdminOrderItemDTO> items;
}
