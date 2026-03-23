package com.shop.dto.user.payment;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentConfirmResponseDTO {
	private Long orderNo;
	private Long amount;
	private String ordererName;
	private LocalDateTime approvedAt;
	private List<PaymentPrepareItemDTO> items;
}
