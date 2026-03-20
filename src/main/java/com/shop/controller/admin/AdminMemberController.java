package com.shop.controller.admin;

import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shop.domain.Coupon;
import com.shop.domain.Inquiry;
import com.shop.domain.LoginHistory;
import com.shop.domain.Member;
import com.shop.domain.MemberCoupon;
import com.shop.domain.MemberMemo;
import com.shop.domain.Orders;
import com.shop.domain.Point;
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

    // 회원 목록 페이징 조회
    // GET /api/admin/member/list?page=1&size=5
    @GetMapping("/list")
    public ResponseEntity<PageResponse<Member>> getMemberList(
            @RequestParam(defaultValue = "1") int    page,
            @RequestParam(defaultValue = "5") int    size,
            @RequestParam(required = false)   String status,
            @RequestParam(required = false)   String keyword) throws Exception {
        AdminMemberSearchDTO dto = new AdminMemberSearchDTO();
        dto.setPage(page);     dto.setSize(size);
        dto.setStatus(status); dto.setKeyword(keyword);
        return ResponseEntity.ok(adminMemberService.getMemberList(dto));
    }

    // 회원 상세 조회
    // GET /api/admin/member/{memberNo}
    @GetMapping("/{memberNo}")
    public ResponseEntity<AdminMemberDetailResponse> getMemberDetail(
            @PathVariable Long memberNo) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberDetail(memberNo));
    }

    // 특정 회원 주문 목록 페이징 조회
    // GET /api/admin/member/{memberNo}/orders?page=1&size=5
    @GetMapping("/{memberNo}/orders")
    public ResponseEntity<PageResponse<Orders>> getMemberOrderPage(
            @PathVariable                     Long memberNo,
            @RequestParam(defaultValue = "1") int  page,
            @RequestParam(defaultValue = "5") int  size) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberOrderPage(memberNo, page, size));
    }

    // 특정 회원 문의 목록 페이징 조회
    // GET /api/admin/member/{memberNo}/inquiries?page=1&size=5
    @GetMapping("/{memberNo}/inquiries")
    public ResponseEntity<PageResponse<Inquiry>> getMemberInquiryPage(
            @PathVariable                     Long memberNo,
            @RequestParam(defaultValue = "1") int  page,
            @RequestParam(defaultValue = "5") int  size) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberInquiryPage(memberNo, page, size));
    }

    // 특정 회원 장바구니 상품 목록 조회
    // GET /api/admin/member/{memberNo}/cart
    @GetMapping("/{memberNo}/cart")
    public ResponseEntity<List<AdminCartItemDTO>> getMemberCartItems(
            @PathVariable Long memberNo) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberCartItems(memberNo));
    }

    // 회원 상태 변경
    // PATCH /api/admin/member/{memberNo}/status?status=SUSPENDED
    @PatchMapping("/{memberNo}/status")
    public ResponseEntity<String> updateMemberStatus(
            @PathVariable Long   memberNo,
            @RequestParam String status) throws Exception {
        adminMemberService.updateMemberStatus(memberNo, status);
        return ResponseEntity.ok("상태가 변경되었습니다.");
    }

    // 회원 정보 수정
    // PUT /api/admin/member/{memberNo}
    @PutMapping("/{memberNo}")
    public ResponseEntity<String> updateMember(
            @PathVariable Long                    memberNo,
            @RequestBody  AdminMemberUpdateRequest request) throws Exception {
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
            return ResponseEntity.internalServerError().body("회원 삭제 중 오류가 발생했습니다.");
        }
    }

    // 특정 회원 로그인 이력 페이징 조회
    // GET /api/admin/member/{memberNo}/login-history?page=1&size=10
    @GetMapping("/{memberNo}/login-history")
    public ResponseEntity<PageResponse<LoginHistory>> getLoginHistoryPage(
            @PathVariable                      Long memberNo,
            @RequestParam(defaultValue = "1")  int  page,
            @RequestParam(defaultValue = "10") int  size) throws Exception {
        return ResponseEntity.ok(adminMemberService.getLoginHistoryPage(memberNo, page, size));
    }

    // 메모 저장
    // POST /api/admin/member/{memberNo}/memo
    @PostMapping("/{memberNo}/memo")
    public ResponseEntity<String> insertMemo(
            @PathVariable Long                    memberNo,
            @RequestBody  Map<String, String> body) throws Exception {
        adminMemberService.insertMemo(memberNo, body.get("content"));
        return ResponseEntity.ok("메모가 저장되었습니다.");
    }

    // 특정 회원 메모 전체 조회
    // GET /api/admin/member/{memberNo}/memo
    @GetMapping("/{memberNo}/memo")
    public ResponseEntity<List<MemberMemo>> getMemosByMemberNo(
            @PathVariable Long memberNo) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemosByMemberNo(memberNo));
    }

    // 메모 삭제
    // DELETE /api/admin/member/memo/{memoNo}
    @DeleteMapping("/memo/{memoNo}")
    public ResponseEntity<String> deleteMemo(
            @PathVariable Long memoNo) throws Exception {
        adminMemberService.deleteMemo(memoNo);
        return ResponseEntity.ok("메모가 삭제되었습니다.");
    }

    // 포인트 지급/차감
    // POST /api/admin/member/{memberNo}/point
    // Body: { "point": 1000, "type": "ADMIN" }
    @PostMapping("/{memberNo}/point")
    public ResponseEntity<String> insertPoint(
            @PathVariable Long                   memberNo,
            @RequestBody  Map<String, Object> body) throws Exception {
        int    point = (int)    body.get("point");
        String type  = (String) body.get("type");
        adminMemberService.insertPoint(memberNo, point, type);
        return ResponseEntity.ok("포인트가 처리되었습니다.");
    }

    // 특정 회원 포인트 잔액 조회
    // GET /api/admin/member/{memberNo}/point/balance
    @GetMapping("/{memberNo}/point/balance")
    public ResponseEntity<Integer> getPointBalance(
            @PathVariable Long memberNo) throws Exception {
        return ResponseEntity.ok(adminMemberService.getPointBalance(memberNo));
    }

    // 특정 회원 포인트 이력 페이징 조회
    // GET /api/admin/member/{memberNo}/point?page=1&size=5
    @GetMapping("/{memberNo}/point")
    public ResponseEntity<PageResponse<Point>> getPointPage(
            @PathVariable                     Long memberNo,
            @RequestParam(defaultValue = "1") int  page,
            @RequestParam(defaultValue = "5") int  size) throws Exception {
        return ResponseEntity.ok(adminMemberService.getPointPage(memberNo, page, size));
    }

    // 쿠폰 생성
    // POST /api/admin/member/coupon
    @PostMapping("/coupon")
    public ResponseEntity<String> insertCoupon(
            @RequestBody Coupon coupon) throws Exception {
        adminMemberService.insertCoupon(coupon);
        return ResponseEntity.ok("쿠폰이 생성되었습니다.");
    }

    // 쿠폰 전체 목록 조회
    // GET /api/admin/member/coupon
    @GetMapping("/coupon")
    public ResponseEntity<List<Coupon>> getAllCoupons() throws Exception {
        return ResponseEntity.ok(adminMemberService.getAllCoupons());
    }

    // 쿠폰 소프트 삭제 여부 변경 (N=정상, Y=삭제)
    // PATCH /api/admin/member/coupon/{couponNo}/delete-yn?deleteYn=Y
    @PatchMapping("/coupon/{couponNo}/delete-yn")
    public ResponseEntity<String> updateCouponDeleteYn(
            @PathVariable Long   couponNo,
            @RequestParam String deleteYn) throws Exception {
        adminMemberService.updateCouponDeleteYn(couponNo, deleteYn);
        return ResponseEntity.ok("쿠폰 상태가 변경되었습니다.");
    }

    // 쿠폰 삭제 (발급된 쿠폰이 있으면 FK 제약으로 실패)
    // DELETE /api/admin/member/coupon/{couponNo}
    @DeleteMapping("/coupon/{couponNo}")
    public ResponseEntity<String> deleteCoupon(
            @PathVariable Long couponNo) {
        try {
            adminMemberService.deleteCoupon(couponNo);
            return ResponseEntity.ok("쿠폰이 삭제되었습니다.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("이미 발급된 쿠폰이 있어 삭제할 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("쿠폰 삭제 중 오류가 발생했습니다.");
        }
    }

 // 특정 회원에게 쿠폰 발급
 // POST /api/admin/member/{memberNo}/coupon
 // Body: { "couponNo": 1, "startAt": "2026-03-19T00:00:00", "endAt": "2026-04-19T23:59:59" }
 @PostMapping("/{memberNo}/coupon")
 public ResponseEntity<String> issueCouponToMember(
         @PathVariable Long                    memberNo,
         @RequestBody  Map<String, Object> body) throws Exception {
     Long   couponNo = Long.valueOf(body.get("couponNo").toString());
     String startAt  = (String) body.get("startAt"); // null 허용 (무기한)
     String endAt    = (String) body.get("endAt");   // null 허용 (무기한)
     adminMemberService.issueCouponToMember(memberNo, couponNo, startAt, endAt);
     return ResponseEntity.ok("쿠폰이 발급되었습니다.");
 }

    // 특정 회원 보유 쿠폰 목록 조회
    // GET /api/admin/member/{memberNo}/coupon
    @GetMapping("/{memberNo}/coupon")
    public ResponseEntity<List<MemberCoupon>> getMemberCoupons(
            @PathVariable Long memberNo) throws Exception {
        return ResponseEntity.ok(adminMemberService.getMemberCoupons(memberNo));
    }

    // 회원 보유 쿠폰 삭제
    // DELETE /api/admin/member/member-coupon/{memberCouponNo}
    @DeleteMapping("/member-coupon/{memberCouponNo}")
    public ResponseEntity<String> deleteMemberCoupon(
            @PathVariable Long memberCouponNo) throws Exception {
        adminMemberService.deleteMemberCoupon(memberCouponNo);
        return ResponseEntity.ok("쿠폰이 삭제되었습니다.");
    }

    // 회원 보유 쿠폰 만료 처리
    // PATCH /api/admin/member/member-coupon/{memberCouponNo}/expire
    @PatchMapping("/member-coupon/{memberCouponNo}/expire")
    public ResponseEntity<String> expireMemberCoupon(
            @PathVariable Long memberCouponNo) throws Exception {
        adminMemberService.expireMemberCoupon(memberCouponNo);
        return ResponseEntity.ok("쿠폰이 만료 처리되었습니다.");
    }
}