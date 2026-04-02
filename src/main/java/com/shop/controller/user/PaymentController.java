package com.shop.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.user.payment.PaymentConfirmRequestDTO;
import com.shop.dto.user.payment.PaymentConfirmResponseDTO;
import com.shop.dto.user.payment.PaymentPrepareRequestDTO;
import com.shop.dto.user.payment.PaymentPrepareResponseDTO;
import com.shop.service.user.payment.PaymentService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public ResponseEntity<PaymentPrepareResponseDTO> preparePayment(
            Authentication authentication,
            @RequestBody PaymentPrepareRequestDTO request
    ) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(paymentService.preparePayment(memberId, request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponseDTO> confirmPayment(
            Authentication authentication,
            @RequestBody PaymentConfirmRequestDTO request
    ) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(paymentService.confirmPayment(memberId, request));
    }

    // 0원 결제 확정 - 쿠폰 전액 할인 시 Toss 없이 직접 주문 완료
    // POST /api/payments/confirm-free
    @PostMapping("/confirm-free")
    public ResponseEntity<PaymentConfirmResponseDTO> confirmFreePayment(
            Authentication authentication,
            @RequestBody PaymentConfirmRequestDTO request
    ) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(paymentService.confirmFreePayment(memberId, request));
    }
}
