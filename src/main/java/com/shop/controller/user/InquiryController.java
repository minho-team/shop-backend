package com.shop.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Member;
import com.shop.dto.user.inquiry.InquiryCreateRequest;
import com.shop.dto.user.inquiry.InquiryPageRequest;
import com.shop.dto.user.inquiry.UpdateInquiryRequest;
import com.shop.service.user.inquiry.InquiryService;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;

// 사용자 1:1 문의 API 컨트롤러
// 관리자 전용 기능은 AdminInquiryController로 분리
@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final MemberService memberService;

    // 문의 작성 (첨부파일 포함)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createInquiry(
            @RequestPart("request") InquiryCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) {
        try {
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            request.setMemberNo(member.getMemberNo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
        return inquiryService.createInquiry(request, files);
    }

    // 내 문의 목록 조회
    @GetMapping("/my")
    public ResponseEntity<?> readMyInquiry(Authentication authentication) {
        try {
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            return inquiryService.readMyInquiry(member.getMemberNo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }

    // 내 문의 페이징 조회
    @GetMapping("/my/page")
    public ResponseEntity<?> getMyInquiryPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            InquiryPageRequest request = new InquiryPageRequest();
            request.setPage(page);
            request.setSize(size);
            request.setMemberNo(member.getMemberNo());
            return inquiryService.getMyInquiryPage(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }

    // 문의 단건 조회 (첨부파일 + 답변 포함)
    @GetMapping("/{inquiryNo}")
    public ResponseEntity<?> readOneInquiry(@PathVariable Long inquiryNo) {
        return inquiryService.readOneInquiry(inquiryNo);
    }

    // 문의 수정 (본인만)
    @PutMapping("/{inquiryNo}")
    public ResponseEntity<?> updateInquiry(
            @PathVariable Long inquiryNo,
            @RequestBody UpdateInquiryRequest dto,
            Authentication authentication) {
        try {
            memberService.readOneMember(authentication.getName());
            return inquiryService.updateInquiry(inquiryNo, dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }

    // 문의 삭제 (본인만, soft delete)
    @DeleteMapping("/{inquiryNo}")
    public ResponseEntity<?> deleteInquiry(
            @PathVariable Long inquiryNo,
            Authentication authentication) {
        try {
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            return inquiryService.deleteInquiry(inquiryNo, member.getMemberNo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
    }
}