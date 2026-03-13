package com.shop.service;

import org.springframework.http.ResponseEntity;
import com.shop.dto.InquiryCreateRequest;
import com.shop.dto.UpdateInquiryRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// 1:1 문의 비즈니스 로직 인터페이스
public interface InquiryService {

    // 문의 작성 (첨부파일 포함)
    ResponseEntity<?> createInquiry(InquiryCreateRequest request, List<MultipartFile> files);

    // 전체 문의 목록 조회 (관리자용)
    ResponseEntity<?> readAllInquiry();

    // 내 문의 목록 조회 (로그인한 회원)
    ResponseEntity<?> readMyInquiry(Long memberNo);

    // 문의 단건 조회 (첨부파일 + 답변 포함)
    ResponseEntity<?> readOneInquiry(Long inquiryNo);

    // 문의 수정
    ResponseEntity<?> updateInquiry(Long inquiryNo, UpdateInquiryRequest dto);

    // 문의 삭제 (본인 글만 - memberNo 체크)
    ResponseEntity<?> deleteInquiry(Long inquiryNo, Long memberNo);

    // =========================================
    // 문의 삭제 (관리자 전용 - memberNo 체크 없이 삭제)
    // =========================================
    ResponseEntity<?> adminDeleteInquiry(Long inquiryNo);
}