package com.shop.service.Impl;

import com.shop.domain.Comment;
import com.shop.domain.Inquiry;
import com.shop.domain.InquiryFile;
import com.shop.dto.InquiryCreateRequest;
import com.shop.dto.UpdateInquiryRequest;
import com.shop.mapper.CommentMapper;
import com.shop.mapper.InquiryFileMapper;
import com.shop.mapper.InquiryMapper;
import com.shop.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 1:1 문의 서비스 구현 클래스
@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    // Mapper 주입
    private final InquiryMapper inquiryMapper;
    private final InquiryFileMapper inquiryFileMapper;
    // 답변 목록 조회를 위한 CommentMapper 주입
    private final CommentMapper commentMapper;

    // =========================================
    // 문의 작성 처리 (첨부파일 포함)
    // =========================================
    @Override
    public ResponseEntity<?> createInquiry(InquiryCreateRequest request, List<MultipartFile> files) {
        try {
            inquiryMapper.createInquiry(request);  // 문의 등록

            // 첨부파일이 있으면 저장
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    // InquiryFile 객체 생성 후 저장
                    InquiryFile inquiryFile = new InquiryFile();
                    inquiryFile.setInquiryNo(request.getInquiryNo());
                    inquiryFile.setFileName(file.getOriginalFilename());
                    inquiryFileMapper.insertFile(inquiryFile);  // 첨부파일 저장
                }
            }
            return ResponseEntity.ok("문의가 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("문의 등록 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 전체 문의 목록 조회 처리 (관리자용)
    // =========================================
    @Override
    public ResponseEntity<?> readAllInquiry() {
        try {
            List<Inquiry> list = inquiryMapper.readAllInquiry();  // 전체 목록 조회
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("문의 목록 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 내 문의 목록 조회 처리 (로그인한 회원)
    // memberNo: 로그인한 회원 번호
    // =========================================
    @Override
    public ResponseEntity<?> readMyInquiry(Long memberNo) {
        try {
            List<Inquiry> list = inquiryMapper.readMyInquiry(memberNo);  // 내 문의 목록 조회
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("내 문의 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 문의 단건 조회 처리 (첨부파일 + 답변 포함)
    // inquiryNo: 조회할 문의 번호
    // Map.of() 대신 HashMap 사용 - null 값 허용을 위해
    // =========================================
    @Override
    public ResponseEntity<?> readOneInquiry(Long inquiryNo) {
        try {
            inquiryMapper.increaseViewCount(inquiryNo);  // 조회수 증가
            Inquiry inquiry = inquiryMapper.readOneInquiry(inquiryNo);  // 문의 단건 조회
            List<InquiryFile> files = inquiryFileMapper.getFilesByInquiryNo(inquiryNo);  // 첨부파일 조회
            List<Comment> comments = commentMapper.getCommentsByInquiryNo(inquiryNo);    // 답변 목록 조회

            // HashMap 사용 - null 값이 있어도 NPE 발생하지 않음
            Map<String, Object> result = new HashMap<>();
            result.put("inquiry", inquiry);
            result.put("files", files != null ? files : new ArrayList<InquiryFile>());        // null 방지
            result.put("comments", comments != null ? comments : new ArrayList<Comment>());   // null 방지

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("문의 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 문의 수정 처리
    // inquiryNo: 수정할 문의 번호
    // =========================================
    @Override
    public ResponseEntity<?> updateInquiry(Long inquiryNo, UpdateInquiryRequest dto) {
        try {
            inquiryMapper.updateInquiry(inquiryNo, dto);  // 문의 수정
            return ResponseEntity.ok("문의가 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("문의 수정 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 문의 삭제 처리 (본인 글만 - memberNo 체크)
    // inquiryNo: 삭제할 문의 번호
    // memberNo: 로그인한 회원 번호 (본인 확인용)
    // =========================================
    @Override
    public ResponseEntity<?> deleteInquiry(Long inquiryNo, Long memberNo) {
        try {
            inquiryMapper.deleteInquiry(inquiryNo, memberNo);  // 본인 글만 삭제
            return ResponseEntity.ok("문의가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("문의 삭제 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 문의 삭제 처리 (관리자 전용 - memberNo 체크 없이 삭제)
    // inquiryNo: 삭제할 문의 번호
    // =========================================
    @Override
    public ResponseEntity<?> adminDeleteInquiry(Long inquiryNo) {
        try {
            inquiryMapper.adminDeleteInquiry(inquiryNo);  // 관리자 전용 삭제 Mapper 호출
            return ResponseEntity.ok("문의가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("문의 삭제 실패: " + e.getMessage());
        }
    }
}