package com.shop.domain;

import java.util.Date;

import lombok.Data;

@Data
public class Orders {
	private Long orderNo;
	private Long memberNo;
	private String ordererName;
	private String ordererPhoneNumber;
	private String ordererEmail;
	private String orderStatus;
	private Long totalPrice;
	private Date createdAt;
	private Date paidAt;
	private String receiverName;
	private String receiverPhoneNumber;
	private String receiverZipCode;
	private String receiverBaseAddress;
	private String receiverDetailAddress;
	private String message;
	private String refundStatus;
	private Date refundRequestedAt;
	private Date refundedAt;
	
}
