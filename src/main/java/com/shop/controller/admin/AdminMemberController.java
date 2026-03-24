package com.shop.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shop.domain.Coupon;
import com.shop.domain.Inquiry;
import com.shop.domain.Member;
import com.shop.domain.MemberMemo;
import com.shop.domain.Orders;
import com.shop.dto.admin.member.AdminCartItemDTO;
import com.shop.dto.admin.member.AdminMemberDetailResponse;
import com.shop.dto.admin.member.AdminMemberSearchDTO;
import com.shop.dto.admin.member.AdminMemberUpdateRequest;
import com.shop.dto.user.inquiry.PageResponse;
import com.shop.service.admin.member.AdminMemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

	private final AdminMemberService adminMemberService;

	// ================================================
	// 1. 회원 기본 관리
	// ================================================

	// 회원 목록 페이징 조회
	// GET /api/admin/member/list?page=1&size=5&status=ACTIVE&keyword=홍길동
	@GetMapping("/list")
	public ResponseEntity<PageResponse<Member>> getMemberList(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String status,
			@RequestParam(required = false) String keyword) throws Exception {
		AdminMemberSearchDTO dto = new AdminMemberSearchDTO();
		dto.setPage(page);
		dto.setSize(size);
		dto.setStatus(status);
		dto.setKeyword(keyword);
		return ResponseEntity.ok(adminMemberService.getMemberList(dto));
	}

	// 회원 상세 조회
	// GET /api/admin/member/{memberNo}
	@GetMapping("/{memberNo}")
	public ResponseEntity<AdminMemberDetailResponse> getMemberDetail(@PathVariable Long memberNo) throws Exception {
		return ResponseEntity.ok(adminMemberService.getMemberDetail(memberNo));
	}

	// 회원 상태 변경
	// PATCH /api/admin/member/{memberNo}/status?status=SUSPENDED
	@PatchMapping("/{memberNo}/status")
	public ResponseEntity<String> updateMemberStatus(@PathVariable Long memberNo, @RequestParam String status)
			throws Exception {
		adminMemberService.updateMemberStatus(memberNo, status);
		return ResponseEntity.ok("상태가 변경되었습니다.");
	}

	// 회원 정보 수정
	// PUT /api/admin/member/{memberNo}
	@PutMapping("/{memberNo}")
	public ResponseEntity<String> updateMember(@PathVariable Long memberNo,
			@RequestBody AdminMemberUpdateRequest request) throws Exception {
		adminMemberService.updateMember(memberNo, request);
		return ResponseEntity.ok("회원 정보가 수정되었습니다.");
	}

	// 회원 삭제
	// DELETE /api/admin/member/{memberNo}
	@DeleteMapping("/{memberNo}")
	public ResponseEntity<String> deleteMember(@PathVariable Long memberNo) {
		try {
			adminMemberService.deleteMember(memberNo);
			return ResponseEntity.ok("회원이 삭제되었습니다.");
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.badRequest().body("주문 내역이 있는 회원은 삭제할 수 없습니다.");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("오류가 발생했습니다.");
		}
	}

	// ================================================
	// 2. 활동 내역 조회
	// ================================================

	// 특정 회원 주문 목록 페이징
	// GET /api/admin/member/{memberNo}/orders?page=1&size=5
	@GetMapping("/{memberNo}/orders")
	public ResponseEntity<PageResponse<Orders>> getMemberOrderPage(@PathVariable Long memberNo,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size) throws Exception {
		return ResponseEntity.ok(adminMemberService.getMemberOrderPage(memberNo, page, size));
	}

	// 특정 회원 문의 목록 페이징
	// GET /api/admin/member/{memberNo}/inquiries?page=1&size=5
	@GetMapping("/{memberNo}/inquiries")
	public ResponseEntity<PageResponse<Inquiry>> getMemberInquiryPage(@PathVariable Long memberNo,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size) throws Exception {
		return ResponseEntity.ok(adminMemberService.getMemberInquiryPage(memberNo, page, size));
	}

	// 특정 회원 장바구니 목록
	// GET /api/admin/member/{memberNo}/cart
	@GetMapping("/{memberNo}/cart")
	public ResponseEntity<List<AdminCartItemDTO>> getMemberCartItems(@PathVariable Long memberNo) throws Exception {
		return ResponseEntity.ok(adminMemberService.getMemberCartItems(memberNo));
	}

	// ================================================
	// 3. 메모 관리
	// ================================================

	// 메모 목록 조회
	// GET /api/admin/member/{memberNo}/memos
	@GetMapping("/{memberNo}/memos")
	public ResponseEntity<List<MemberMemo>> getMemberMemos(@PathVariable Long memberNo) throws Exception {
		return ResponseEntity.ok(adminMemberService.getMemberMemoList(memberNo));
	}

	// 메모 등록
	// POST /api/admin/member/memos
	@PostMapping("/memos")
	public ResponseEntity<String> addMemo(@RequestBody MemberMemo memo) throws Exception {
		adminMemberService.addMemberMemo(memo);
		return ResponseEntity.ok("메모가 등록되었습니다.");
	}

	// 메모 삭제
	// DELETE /api/admin/member/memos/{memoNo}
	@DeleteMapping("/memos/{memoNo}")
	public ResponseEntity<String> deleteMemo(@PathVariable Long memoNo) throws Exception {
		adminMemberService.deleteMemberMemo(memoNo);
		return ResponseEntity.ok("메모가 삭제되었습니다.");
	}

	// ================================================
	// 4. 쿠폰 관리
	// ================================================

	// 회원 보유 쿠폰 목록 조회
	// GET /api/admin/member/{memberNo}/coupons
	@GetMapping("/{memberNo}/coupons")
	public ResponseEntity<List<Map<String, Object>>> getMemberCoupons(@PathVariable Long memberNo) throws Exception {
		return ResponseEntity.ok(adminMemberService.getMemberCouponList(memberNo));
	}

	// 발급 가능한 전체 쿠폰 마스터 목록
	// GET /api/admin/member/coupons/master
	@GetMapping("/coupons/master")
	public ResponseEntity<List<Coupon>> getCouponMasterList() throws Exception {
		return ResponseEntity.ok(adminMemberService.getCouponMasterList());
	}

	// 회원에게 쿠폰 발급
	// POST /api/admin/member/{memberNo}/coupons/issue?couponNo=1&validDays=30
	@PostMapping("/{memberNo}/coupons/issue")
	public ResponseEntity<String> issueCoupon(@PathVariable Long memberNo, @RequestParam Long couponNo,
			@RequestParam(defaultValue = "30") int validDays) throws Exception {
		adminMemberService.issueCoupon(memberNo, couponNo, validDays);
		return ResponseEntity.ok("쿠폰이 발급되었습니다.");
	}

	// 쿠폰 마스터 생성
	// POST /api/admin/member/coupons
	@PostMapping("/coupons")
	public ResponseEntity<String> createCoupon(@RequestBody Coupon coupon) throws Exception {
		adminMemberService.createCoupon(coupon);
		return ResponseEntity.ok("쿠폰이 생성되었습니다.");
	}
}