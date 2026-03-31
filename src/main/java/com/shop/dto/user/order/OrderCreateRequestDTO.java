package com.shop.dto.user.order;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderCreateRequestDTO {
	private Long orderNo;
	
	private String ordererName;
	private String ordererPhoneNumber;
	private String ordererEmail;

	private String receiverName;
	private String receiverPhoneNumber;
	private String receiverZipCode;
	private String receiverBaseAddress;
	private String receiverDetailAddress;

	private String message;
	private Long totalPrice;

	private List<OrderItemCreateRequestDTO> items;
}