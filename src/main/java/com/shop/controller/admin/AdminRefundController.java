package com.shop.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.dto.admin.refund.AdminRefundListRequestDTO;
import com.shop.dto.admin.refund.AdminRefundStatusUpdateRequestDTO;
import com.shop.service.admin.refund.AdminRefundService;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/refund")
public class AdminRefundController {

	private final AdminRefundService adminRefundService;
	private final MemberService memberService;

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

	// 환불아이템에 대한 승인/거절 의사결정
	@PutMapping("/{refundNo}")
	public ResponseEntity<?> decideRefundStatus(@PathVariable Long refundNo,
			@RequestBody AdminRefundStatusUpdateRequestDTO request,Authentication authentication) {
		log.info("환불 의사결정 컨트롤러 진입 status:"+request.getStatus());
		
		String memberId = authentication.getName();
		try {
			Member member = memberService.readOneMember(memberId);
			adminRefundService.decideRefund(member.getMemberNo(),refundNo, request.getStatus());
			return ResponseEntity.ok("환불 의사 결정 완료:"+request.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin/refund: decideRefundStatus오류 발생");

		}
	}

}
