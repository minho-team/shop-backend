package com.shop.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.user.refund.RefundCreateRequestDTO;
import com.shop.service.user.refund.RefundService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/refund")
@RequiredArgsConstructor
public class RefundController {

	private final RefundService refundService;

	@PostMapping
	public ResponseEntity<String> createRefund(@RequestBody RefundCreateRequestDTO requestDTO,
			Authentication authentication) {
		String memberId = authentication.getName();
		log.info("createRefund 진입");
		try {
			refundService.createRefund(memberId, requestDTO);
			return ResponseEntity.ok("환불 신청 완료");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("환불 생성 실패: " + e.getMessage());

		}
	}
}
