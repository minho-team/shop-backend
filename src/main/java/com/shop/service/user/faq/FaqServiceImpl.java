package com.shop.service.user.faq;

import com.shop.domain.Faq;
import com.shop.dto.user.inquiry.FaqCreateRequest;
import com.shop.dto.user.inquiry.FaqPageRequest;
import com.shop.dto.user.inquiry.PageResponse;
import com.shop.mapper.user.FaqMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

// FAQ 서비스 구현 클래스
@Service
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService {

    // Mapper 주입
    private final FaqMapper faqMapper;

    // =========================================
    // FAQ 전체 조회 처리
    // =========================================
    @Override
    public ResponseEntity<?> readAllFaq() {
        try {
            List<Faq> list = faqMapper.readAllFaq();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 카테고리별 FAQ 조회 처리
    // =========================================
    @Override
    public ResponseEntity<?> readFaqByCategory(String category) {
        try {
            List<Faq> list = faqMapper.readFaqByCategory(category);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 카테고리 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // FAQ 키워드 검색 처리
    // =========================================
    @Override
    public ResponseEntity<?> searchFaq(String keyword) {
        try {
            List<Faq> list = faqMapper.searchFaq(keyword);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 검색 실패: " + e.getMessage());
        }
    }

    // =========================================
    // FAQ 페이징 조회 처리
    // 1. 전체 건수 조회 (countFaq) → totalCount
    // 2. 현재 페이지 데이터 조회 (getFaqPage) → list
    // 3. PageResponse로 묶어서 반환
    // =========================================
    @Override
    public ResponseEntity<?> getFaqPage(FaqPageRequest request) {
        try {
            // 전체 건수 조회 (페이징 계산용)
            int totalCount = faqMapper.countFaq(request);
            // 현재 페이지 데이터 조회
            List<Faq> list = faqMapper.getFaqPage(request);
            // PageResponse 생성 후 반환
            PageResponse<Faq> response = new PageResponse<>(list, totalCount, request.getPage(), request.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 페이징 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // FAQ 등록 처리 (관리자 전용)
    // =========================================
    @Override
    public ResponseEntity<?> createFaq(FaqCreateRequest request) {
        try {
            faqMapper.createFaq(request);  // FAQ 등록 Mapper 호출
            return ResponseEntity.ok("FAQ가 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 등록 실패: " + e.getMessage());
        }
    }

    // =========================================
    // FAQ 삭제 처리 (관리자 전용, soft delete)
    // faqNo: 삭제할 FAQ 번호
    // =========================================
    @Override
    public ResponseEntity<?> deleteFaq(Long faqNo) {
        try {
            faqMapper.deleteFaq(faqNo);  // FAQ 삭제 Mapper 호출 (delete_yn = 'Y')
            return ResponseEntity.ok("FAQ가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 삭제 실패: " + e.getMessage());
        }
    }
}
