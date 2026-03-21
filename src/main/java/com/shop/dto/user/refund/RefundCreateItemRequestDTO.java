package com.shop.dto.user.refund;

import lombok.Data;

@Data
public class RefundCreateItemRequestDTO {
    private Long orderItemNo;
    private Integer refundQuantity;
    private Long refundAmount;
}