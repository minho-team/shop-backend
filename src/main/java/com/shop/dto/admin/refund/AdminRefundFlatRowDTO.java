package com.shop.dto.admin.refund;

import lombok.Data;

@Data
public class AdminRefundFlatRowDTO {
    private Long refundNo;
    private Long orderNo;
    private String refundStatus;
    private String requestedAt;
    private String memberId;
    private String name;
    private Long totalRefundAmount;

    private Long refundItemNo;
    private Long orderItemNo;
    private String itemName;
    private String itemColor;
    private String itemSize;
    private Integer refundQuantity;
    private Long refundAmount;
    private String refundItemStatus;
}
