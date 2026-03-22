package com.shop.service.user.payment;

import com.shop.dto.user.payment.PaymentConfirmRequestDTO;
import com.shop.dto.user.payment.PaymentConfirmResponseDTO;
import com.shop.dto.user.payment.PaymentPrepareRequestDTO;
import com.shop.dto.user.payment.PaymentPrepareResponseDTO;

public interface PaymentService {
	PaymentPrepareResponseDTO preparePayment(String memberId, PaymentPrepareRequestDTO request);

	PaymentConfirmResponseDTO confirmPayment(String memberId, PaymentConfirmRequestDTO request);
}
