package com.shop.service;

import org.springframework.http.ResponseEntity;

import com.shop.dto.user.inquiry.FaqCreateRequest;

// FAQ 비즈니스 로직 인터페이스
public interface FaqService {

    // FAQ 전체 조회
    ResponseEntity<?> readAllFaq();

    // 카테고리별 FAQ 조회
    ResponseEntity<?> readFaqByCategory(String category);

    // FAQ 키워드 검색
    ResponseEntity<?> searchFaq(String keyword);

    // FAQ 등록 (관리자 전용)
    ResponseEntity<?> createFaq(FaqCreateRequest request);

    // FAQ 삭제 (관리자 전용, soft delete)
    ResponseEntity<?> deleteFaq(Long faqNo);
}