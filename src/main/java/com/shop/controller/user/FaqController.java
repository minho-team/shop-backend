package com.shop.controller.user;

import com.shop.dto.user.inquiry.FaqCreateRequest;
import com.shop.dto.user.inquiry.FaqPageRequest;
import com.shop.service.user.faq.FaqService;

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
    // FAQ 페이징 조회 API (비로그인 허용)
    // 사용자/관리자 FAQ 페이지에서 호출
    // 쿼리 파라미터:
    //   page: 페이지 번호 (기본값 1)
    //   size: 페이지당 개수 (기본값 10)
    //   category: 카테고리 필터 (생략 또는 "전체"면 전체 조회)
    //   keyword: 검색 키워드 (생략하면 검색 안 함)
    // 반환값: PageResponse<Faq> { list, totalCount, totalPages, currentPage, pageSize }
    // =========================================
    @GetMapping("/page")
    public ResponseEntity<?> getFaqPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        // FaqPageRequest 객체 생성 후 파라미터 주입
        FaqPageRequest request = new FaqPageRequest();
        request.setPage(page);
        request.setSize(size);
        request.setCategory(category);
        request.setKeyword(keyword);

        return faqService.getFaqPage(request);
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
