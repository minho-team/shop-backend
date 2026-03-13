package com.shop.controller;

import com.shop.dto.InquiryCreateRequest;
import com.shop.dto.UpdateInquiryRequest;
import com.shop.service.InquiryService;
import com.shop.service.MemberService; // [수정] MemberService 추가
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// 1:1 문의 게시판 API를 처리하는 REST 컨트롤러
@RestController
// 게시판 API 기본 경로
@RequestMapping("/api/inquiry")
// final 필드를 사용하는 생성자 자동 생성
@RequiredArgsConstructor
public class InquiryController {

    // 게시판 서비스 객체
    private final InquiryService inquiryService;
    // [수정] memberId → memberNo 변환을 위해 MemberService 추가
    private final MemberService memberService;

    // 게시글 작성 API (첨부파일 포함 - multipart/form-data)
    // request: 게시글 정보 JSON, files: 첨부 이미지 (선택)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createInquiry(
            @RequestPart("request") InquiryCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal String username) {

        try {
            // [수정] JWT subject는 memberId(문자열)이므로 memberNo(숫자)로 변환 후 주입
            Long memberNo = memberService.readOneMember(username).getMemberNo();
            request.setMemberNo(memberNo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }

        return inquiryService.createInquiry(request, files);
    }

    // 게시글 전체 조회 API - 관리자용
    @GetMapping
    public ResponseEntity<?> readAllInquiry() {
        return inquiryService.readAllInquiry();
    }

    // 내 1:1문의 내역 조회 API (로그인한 회원의 게시글만)
    @GetMapping("/my")
    public ResponseEntity<?> readMyInquiry(@AuthenticationPrincipal String username) {
        // JWT에서 추출한 회원번호로 내 글만 조회
        try {
            Long memberNo = memberService.readOneMember(username).getMemberNo();
            return inquiryService.readMyInquiry(memberNo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }

    // 1:1문의글 하나 조회 API (첨부파일 + 관리자 답변 포함)
    @GetMapping("/{inquiryNo}")
    public ResponseEntity<?> readOneInquiry(@PathVariable Long inquiryNo) {
        return inquiryService.readOneInquiry(inquiryNo);
    }

    // 1:1문의글 수정 API
    @PutMapping("/{inquiryNo}")
    public ResponseEntity<?> updateInquiry(
            @PathVariable Long inquiryNo,
            @RequestBody UpdateInquiryRequest dto) {
        return inquiryService.updateInquiry(inquiryNo, dto);
    }

    // 1:1문의글 삭제 API (본인 글만 삭제 가능)
    @DeleteMapping("/{inquiryNo}")
    public ResponseEntity<?> deleteInquiryNo(
            @PathVariable Long inquiryNo,
            @AuthenticationPrincipal String username) {
        // JWT에서 추출한 회원번호로 본인 확인
        try {
            Long memberNo = memberService.readOneMember(username).getMemberNo();
            return inquiryService.deleteInquiry(inquiryNo, memberNo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }
}