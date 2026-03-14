package com.shop.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Member;
import com.shop.dto.InquiryCreateRequest;
import com.shop.dto.UpdateInquiryRequest;
import com.shop.service.InquiryService;
import com.shop.service.MemberService;

import lombok.RequiredArgsConstructor;

// 1:1 문의 게시판 API를 처리하는 REST 컨트롤러
@RestController
// 게시판 API 기본 경로
@RequestMapping("/api/inquiry")
// final 필드를 사용하는 생성자 자동 생성
@RequiredArgsConstructor
public class InquiryController {

    // 게시판 서비스 객체
    private final InquiryService inquiryService;
    // memberId → memberNo 변환을 위해 MemberService 추가
    private final MemberService memberService;

    // =========================================
    // 게시글 작성 API (첨부파일 포함 - multipart/form-data)
    // request: 게시글 정보 JSON, files: 첨부 이미지 (선택)
    // =========================================
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createInquiry(
            @RequestPart("request") InquiryCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) {

        try {
            // 토큰에서 memberId 추출 후 memberNo 변환하여 요청에 주입
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            request.setMemberNo(member.getMemberNo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }

        return inquiryService.createInquiry(request, files);
    }

    // =========================================
    // 게시글 전체 조회 API (관리자용)
    // =========================================
    @GetMapping
    public ResponseEntity<?> readAllInquiry() {
        // 전체 문의 목록 반환
        return inquiryService.readAllInquiry();
    }

    // =========================================
    // 내 1:1 문의 내역 조회 API (로그인한 회원의 게시글만)
    // =========================================
    @GetMapping("/my")
    public ResponseEntity<?> readMyInquiry(Authentication authentication) {
        try {
            // 토큰에서 memberId 추출 후 memberNo 변환하여 내 글만 조회
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            return inquiryService.readMyInquiry(member.getMemberNo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 1:1 문의글 하나 조회 API (첨부파일 + 관리자 답변 포함)
    // inquiryNo: 조회할 문의 번호
    // =========================================
    @GetMapping("/{inquiryNo}")
    public ResponseEntity<?> readOneInquiry(@PathVariable Long inquiryNo) {
        return inquiryService.readOneInquiry(inquiryNo);
    }

    // =========================================
    // 1:1 문의글 수정 API
    // inquiryNo: 수정할 문의 번호
    // =========================================
    @PutMapping("/{inquiryNo}")
    public ResponseEntity<?> updateInquiry(
            @PathVariable Long inquiryNo,
            @RequestBody UpdateInquiryRequest dto,
            Authentication authentication) {

        try {
            // 토큰에서 memberId 추출 (본인 확인용)
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            return inquiryService.updateInquiry(inquiryNo, dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 1:1 문의글 삭제 API (본인 글만 삭제 가능)
    // inquiryNo: 삭제할 문의 번호
    // =========================================
    @DeleteMapping("/{inquiryNo}")
    public ResponseEntity<?> deleteInquiry(
            @PathVariable Long inquiryNo,
            Authentication authentication) {

        try {
            // 토큰에서 memberId 추출 후 memberNo 변환하여 본인 확인
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            return inquiryService.deleteInquiry(inquiryNo, member.getMemberNo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }

    // =========================================
    // 문의 삭제 API (관리자 전용 - 모든 문의 삭제 가능)
    // inquiryNo: 삭제할 문의 번호
    // =========================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{inquiryNo}")
    public ResponseEntity<?> adminDeleteInquiry(@PathVariable Long inquiryNo) {
        return inquiryService.adminDeleteInquiry(inquiryNo);
    }
}