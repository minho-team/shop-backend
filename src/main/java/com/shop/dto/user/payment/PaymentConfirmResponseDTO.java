package com.shop.dto.user.payment;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentConfirmResponseDTO {
    private Long orderNo;
    private Long amount;
    private String ordererName;
    private LocalDateTime approvedAt;
}
