package com.shop.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.shop.dto.user.inquiry.FaqCreateRequest;
import com.shop.mapper.admin.AdminFaqMapper;

import lombok.RequiredArgsConstructor;

// 관리자 전용 FAQ API 컨트롤러
@RestController
@RequestMapping("/api/admin/faq")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFaqController {

    // ★ FaqService 대신 AdminFaqMapper 직접 사용 (등록/삭제만 필요)
    private final AdminFaqMapper adminFaqMapper;

    // FAQ 등록
    // POST /api/admin/faq
    @PostMapping
    public ResponseEntity<?> createFaq(@RequestBody FaqCreateRequest request) {
        try {
            // sortOrder 미입력(null) 또는 0 이하면 0으로 설정 → ORDER BY sort_order ASC 기준 맨 앞
            if (request.getSortOrder() == null || request.getSortOrder() <= 0) {
                request.setSortOrder(0);
            }
            adminFaqMapper.createFaq(request);
            return ResponseEntity.ok("FAQ가 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 등록 실패: " + e.getMessage());
        }
    }

    // FAQ 삭제 (soft delete)
    // DELETE /api/admin/faq/{faqNo}
    @DeleteMapping("/{faqNo}")
    public ResponseEntity<?> deleteFaq(@PathVariable Long faqNo) {
        try {
            adminFaqMapper.deleteFaq(faqNo);
            return ResponseEntity.ok("FAQ가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 삭제 실패: " + e.getMessage());
        }
    }
}