package com.shop.dto.user.payment;

import java.util.List;

import lombok.Data;

@Data
public class PaymentConfirmRequestDTO {
	private String paymentKey;
	private String orderId;
	private Long amount;
	private List<Long> orderedCartItemNos;
	private Long memberCouponNo;
}
