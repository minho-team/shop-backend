package com.shop.dto.user.refund;

import java.util.List;

import lombok.Data;

@Data
public class RefundCreateRequestDTO {
    private Long orderNo;
    private String refundReason;
    private String bankName;
    private String bankCode;
    private List<RefundCreateItemRequestDTO> items;
}