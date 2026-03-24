package com.shop.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.shop.dto.user.inquiry.InquiryPageRequest;
import com.shop.service.user.inquiry.InquiryService;

import lombok.RequiredArgsConstructor;

// 관리자 전용 1:1 문의 API 컨트롤러
// 사용자 기능은 InquiryController에 있음
@RestController
@RequestMapping("/api/admin/inquiry")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInquiryController {

    private final InquiryService inquiryService;

    // 전체 문의 목록 조회
    @GetMapping
    public ResponseEntity<?> readAllInquiry() {
        return inquiryService.readAllInquiry();
    }

    // 전체 문의 페이징 조회
    @GetMapping("/page")
    public ResponseEntity<?> getInquiryPage(
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") String size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        try {
            InquiryPageRequest request = new InquiryPageRequest();
            request.setPage(Integer.parseInt(page));
            request.setSize(Integer.parseInt(size));
            request.setStatus(status);
            request.setCategory(category);
            request.setKeyword(keyword);
            return inquiryService.getInquiryPage(request);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("잘못된 페이지 번호 형식입니다.");
        }
    }

    // 문의 삭제 (관리자 - 모든 문의 삭제 가능, soft delete)
    @DeleteMapping("/{inquiryNo}")
    public ResponseEntity<?> adminDeleteInquiry(@PathVariable Long inquiryNo) {
        return inquiryService.adminDeleteInquiry(inquiryNo);
    }
}