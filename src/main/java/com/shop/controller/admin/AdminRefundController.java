package com.shop.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.admin.refund.AdminRefundDetailResponseDTO;
import com.shop.dto.admin.refund.AdminRefundListRequestDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateRequestDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateResponseDTO;
import com.shop.service.admin.refund.AdminRefundService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/refund")
public class AdminRefundController {

	private final AdminRefundService adminRefundService;

	@GetMapping("/list")
	public ResponseEntity<?> getRefundList(@ModelAttribute AdminRefundListRequestDTO request) {
		try {
			return ResponseEntity.ok(adminRefundService.getRefundList(request));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin/refund/list 오류 발생");

		}
	}

	@GetMapping("/{refundNo}")
	public ResponseEntity<?> getRefundDetail(@PathVariable Long refundNo) {
		try {
			return ResponseEntity.ok(adminRefundService.getRefundDetail(refundNo));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin/refund: getRefundDetail오류 발생");

		}
	}

	@PutMapping("/{refundNo}/status")
	public ResponseEntity<?> updateRefundStatus(@PathVariable Long refundNo,
			@RequestBody AdminRefundStatusUpdateRequestDTO request) {
		try {
			return ResponseEntity.ok(adminRefundService.updateRefundStatus(refundNo, request));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin/refund: updateRefundStatus오류 발생");

		}
	}
}
