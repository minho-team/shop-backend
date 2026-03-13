package com.shop.controller;

import com.shop.dto.FaqCreateRequest;
import com.shop.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// FAQ API를 처리하는 REST 컨트롤러
@RestController
// FAQ API 기본 경로
@RequestMapping("/api/faq")
// final 필드를 사용하는 생성자 자동 생성
@RequiredArgsConstructor
public class FaqController {

    // FAQ 서비스 객체
    private final FaqService faqService;

    // =========================================
    // FAQ 전체 조회 API (비로그인 허용)
    // =========================================
    @GetMapping
    public ResponseEntity<?> readAllFaq() {
        return faqService.readAllFaq();
    }

    // =========================================
    // 카테고리별 FAQ 조회 API (비로그인 허용)
    // category: 조회할 카테고리명 (예: 배송, 주문/결제 등)
    // =========================================
    @GetMapping("/category")
    public ResponseEntity<?> readFaqByCategory(@RequestParam String category) {
        return faqService.readFaqByCategory(category);
    }

    // =========================================
    // FAQ 키워드 검색 API (비로그인 허용)
    // keyword: 질문 또는 답변에서 검색할 키워드
    // =========================================
    @GetMapping("/search")
    public ResponseEntity<?> searchFaq(@RequestParam String keyword) {
        return faqService.searchFaq(keyword);
    }

    // =========================================
    // FAQ 등록 API (관리자 전용)
    // request: { category, question, answer, sortOrder }
    // =========================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createFaq(@RequestBody FaqCreateRequest request) {
        return faqService.createFaq(request);
    }

    // =========================================
    // FAQ 삭제 API (관리자 전용, soft delete)
    // faqNo: 삭제할 FAQ 번호
    // =========================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{faqNo}")
    public ResponseEntity<?> deleteFaq(@PathVariable Long faqNo) {
        return faqService.deleteFaq(faqNo);
    }
}