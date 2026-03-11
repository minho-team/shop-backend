package com.shop.service;

import com.shop.dto.InquiryCreateRequest;
import com.shop.dto.UpdateInquiryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// 1:1 문의 게시판 비즈니스 로직 인터페이스
public interface InquiryService {

    // 게시글 작성 (첨부파일 포함)
    ResponseEntity<?> createInquiry(InquiryCreateRequest request, List<MultipartFile> files);

    // 게시글 전체 조회 (관리자용)
    ResponseEntity<?> readAllInquiry();

    // 내 문의 내역 조회 (로그인한 회원)
    ResponseEntity<?> readMyInquiry(Long memberNo);

    // 게시글 하나 조회 (첨부파일 + 댓글 포함)
    ResponseEntity<?> readOneInquiry(Long inquiryNo);

    // 게시글 수정
    ResponseEntity<?> updateInquiry(Long inquiryNo, UpdateInquiryRequest dto);

    // 게시글 삭제 (본인 글만 가능)
    ResponseEntity<?> deleteInquiry(Long inquiryNo, Long memberNo);
}
