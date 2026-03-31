package com.shop.dto.user.payment;

import java.util.List;

import lombok.Data;

@Data
public class PaymentPrepareRequestDTO {
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
    private Long memberCouponNo;
    private List<PaymentPrepareItemDTO> items;
}
