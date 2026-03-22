package com.shop.dto.user.payment;

import lombok.Data;

@Data
public class PaymentConfirmRequestDTO {
    private String paymentKey;
    private String orderId;
    private Long amount;
}
