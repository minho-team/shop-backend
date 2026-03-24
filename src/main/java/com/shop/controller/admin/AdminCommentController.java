package com.shop.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.shop.domain.Member;
import com.shop.dto.user.inquiry.CommentCreateRequest;
import com.shop.service.user.comment.CommentService;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;

// 관리자 전용 1:1 문의 답변(댓글) API 컨트롤러
// 답변 작성/삭제는 관리자만 가능
@RestController
@RequestMapping("/api/admin/comment")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    // 답변 작성 (관리자 전용)
    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestBody CommentCreateRequest request,
            Authentication authentication) {
        try {
            String memberId = authentication.getName();
            Member member = memberService.readOneMember(memberId);
            request.setMemberNo(member.getMemberNo());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보 조회 실패: " + e.getMessage());
        }
        return commentService.createComment(request);
    }

    // 답변 삭제 (관리자 전용)
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentNo) {
        return commentService.deleteComment(commentNo);
    }
}