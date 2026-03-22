package com.shop.dto.admin.refund;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRefundStatusUpdateResponseDTO {
    private Long refundNo;
    private String refundStatus;
    private String message;
}
