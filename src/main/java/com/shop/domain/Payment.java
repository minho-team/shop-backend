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
    private Long paymentAmount;
    private String pgProvider;
    private String pgTid;
    private String paymentKey;
    private LocalDateTime approvedAt;
    private LocalDateTime failedAt;
    private LocalDateTime canceledAt;
    private String failReason;
    private LocalDateTime createdAt;
}
