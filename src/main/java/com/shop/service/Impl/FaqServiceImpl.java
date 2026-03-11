package com.shop.service.Impl;

import com.shop.domain.Faq;
import com.shop.mapper.FaqMapper;
import com.shop.service.FaqService;
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

    // FAQ 전체 조회 처리
    @Override
    public ResponseEntity<?> readAllFaq() {
        try {
            List<Faq> list = faqMapper.readAllFaq();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 조회 실패: " + e.getMessage());
        }
    }

    // 카테고리별 FAQ 조회 처리
    @Override
    public ResponseEntity<?> readFaqByCategory(String category) {
        try {
            List<Faq> list = faqMapper.readFaqByCategory(category);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 카테고리 조회 실패: " + e.getMessage());
        }
    }

    // FAQ 키워드 검색 처리
    @Override
    public ResponseEntity<?> searchFaq(String keyword) {
        try {
            List<Faq> list = faqMapper.searchFaq(keyword);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FAQ 검색 실패: " + e.getMessage());
        }
    }
}
