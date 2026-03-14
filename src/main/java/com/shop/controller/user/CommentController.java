package com.shop.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.dto.user.inquiry.CommentCreateRequest;
import com.shop.service.CommentService;
import com.shop.service.MemberService;

import lombok.RequiredArgsConstructor;

// 관리자 답변(댓글) API를 처리하는 REST 컨트롤러
@RestController
// 답변 API 기본 경로
@RequestMapping("/api/comment")
// final 필드를 사용하는 생성자 자동 생성
@RequiredArgsConstructor
public class CommentController {

    // 답변 서비스 객체
    private final CommentService commentService;
    private final MemberService memberService;

    // 답변 작성 API (관리자 전용)
    // request: 답변 내용 JSON, user: JWT 인증 정보
    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestBody CommentCreateRequest request,
            Authentication authentication) {
        // JWT에서 추출한 관리자 회원번호를 request에 주입
    	String memberId = authentication.getName();
    	Member member = null;
		try {
			member = memberService.readOneMember(memberId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        request.setMemberNo(member.getMemberNo());
        return commentService.createComment(request);
    }

    // 답변 삭제 API (관리자 전용)
    // commentNo: 삭제할 답변 번호
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentNo) {
        return commentService.deleteComment(commentNo);
    }
}