package com.shop.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Long paymentNo;
    private Long orderNo;
    private Long memberNo;
    private String paymentMethod;
    private String paymentStatus;
    // 실제 결제 요청/승인 금액
    private Long paymentAmount;
    // 결제 시 반영된 쿠폰 할인금액
    private Long discountAmount;
    private String pgProvider;
    private String pgTid;
    private String paymentKey;
    private LocalDateTime approvedAt;
    private LocalDateTime failedAt;
    private LocalDateTime canceledAt;
    private String failReason;
    private LocalDateTime createdAt;
}
