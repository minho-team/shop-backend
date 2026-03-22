package com.shop.dto.admin.refund;

import java.util.List;
import lombok.Data;

@Data
public class AdminRefundListResponseDTO {
    private Long refundNo;
    private Long orderNo;
    private String refundStatus;
    private String requestedAt;
    private String memberId;
    private String name;
    private Long totalRefundAmount;
    private List<AdminRefundListItemDTO> items;
}
