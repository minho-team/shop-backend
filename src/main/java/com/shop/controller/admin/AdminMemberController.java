package com.shop.controller.admin;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Inquiry;
import com.shop.domain.Member;
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
    // 회원 목록 페이징 조회
    // GET /api/admin/member/list?page=1&size=5&status=ACTIVE&keyword=홍
    // ================================================
    @GetMapping("/list")
    public ResponseEntity<PageResponse<Member>> getMemberList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) throws Exception {

        AdminMemberSearchDTO dto = new AdminMemberSearchDTO();
        dto.setPage(page);
        dto.setSize(size);
        dto.setStatus(status);
        dto.setKeyword(keyword);
        return ResponseEntity.ok(adminMemberService.getMemberList(dto));
    }

    // ================================================
    // 회원 상세 조회
    // GET /api/admin/member/{memberNo}
    // ================================================
    @GetMapping("/{memberNo}")
    public ResponseEntity<AdminMemberDetailResponse> getMemberDetail(
            @PathVariable Long memberNo) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberDetail(memberNo));
    }

    // ================================================
    // 특정 회원 주문 목록 페이징 조회 (5개씩)
    // GET /api/admin/member/{memberNo}/orders?page=1&size=5
    // ================================================
    @GetMapping("/{memberNo}/orders")
    public ResponseEntity<PageResponse<Orders>> getMemberOrderPage(
            @PathVariable Long memberNo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberOrderPage(memberNo, page, size));
    }

    // ================================================
    // 특정 회원 문의 목록 페이징 조회 (5개씩)
    // GET /api/admin/member/{memberNo}/inquiries?page=1&size=5
    // ================================================
    @GetMapping("/{memberNo}/inquiries")
    public ResponseEntity<PageResponse<Inquiry>> getMemberInquiryPage(
            @PathVariable Long memberNo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberInquiryPage(memberNo, page, size));
    }

    // ================================================
    // 특정 회원 장바구니 상품 목록 조회
    // GET /api/admin/member/{memberNo}/cart
    // ================================================
    @GetMapping("/{memberNo}/cart")
    public ResponseEntity<List<AdminCartItemDTO>> getMemberCartItems(
            @PathVariable Long memberNo) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberCartItems(memberNo));
    }

    // ================================================
    // 회원 상태 변경
    // PATCH /api/admin/member/{memberNo}/status?status=SUSPENDED
    // ================================================
    @PatchMapping("/{memberNo}/status")
    public ResponseEntity<String> updateMemberStatus(
            @PathVariable Long memberNo,
            @RequestParam String status) throws Exception {
        adminMemberService.updateMemberStatus(memberNo, status);
        return ResponseEntity.ok("상태가 변경되었습니다.");
    }

    // ================================================
    // 회원 정보 수정
    // PUT /api/admin/member/{memberNo}
    // ================================================
    @PutMapping("/{memberNo}")
    public ResponseEntity<String> updateMember(
            @PathVariable Long memberNo,
            @RequestBody AdminMemberUpdateRequest request) throws Exception {
        adminMemberService.updateMember(memberNo, request);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // ================================================
    // 회원 삭제
    // DELETE /api/admin/member/{memberNo}
    // ================================================
    @DeleteMapping("/{memberNo}")
    public ResponseEntity<String> deleteMember(@PathVariable Long memberNo) {
        try {
            adminMemberService.deleteMember(memberNo);
            return ResponseEntity.ok("회원이 삭제되었습니다.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("주문 내역이 있는 회원은 삭제할 수 없습니다. 상태를 정지(SUSPENDED)로 변경하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("회원 삭제 중 오류가 발생했습니다.");
        }
    }
}