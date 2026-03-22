package com.shop.dto.user.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentPrepareResponseDTO {
    private Long orderNo;
    private String orderId;
    private String orderName;
    private Long amount;

    private String customerName;
    private String customerEmail;
    private String customerMobilePhone;
}