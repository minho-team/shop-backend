package com.shop.dto.admin.refund;

import lombok.Data;

@Data
public class AdminRefundDetailItemDTO {
    private Long refundItemNo;
    private Long orderItemNo;
    private String itemName;
    private String itemColor;
    private String itemSize;
    private Integer refundQuantity;
    private Long refundAmount;
    private String refundItemStatus;

}
