package com.shop.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shop.dto.user.inquiry.FaqPageRequest;
import com.shop.service.user.faq.FaqService;

import lombok.RequiredArgsConstructor;

// 사용자 FAQ API 컨트롤러
// 관리자 전용 기능은 AdminFaqController로 분리
@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    // FAQ 전체 조회 (비로그인 허용)
    @GetMapping
    public ResponseEntity<?> readAllFaq() {
        return faqService.readAllFaq();
    }

    // 카테고리별 FAQ 조회 (비로그인 허용)
    @GetMapping("/category")
    public ResponseEntity<?> readFaqByCategory(@RequestParam String category) {
        return faqService.readFaqByCategory(category);
    }

    // FAQ 키워드 검색 (비로그인 허용)
    @GetMapping("/search")
    public ResponseEntity<?> searchFaq(@RequestParam String keyword) {
        return faqService.searchFaq(keyword);
    }

    // FAQ 페이징 조회 (비로그인 허용)
    @GetMapping("/page")
    public ResponseEntity<?> getFaqPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        FaqPageRequest request = new FaqPageRequest();
        request.setPage(page);
        request.setSize(size);
        request.setCategory(category);
        request.setKeyword(keyword);
        return faqService.getFaqPage(request);
    }
}