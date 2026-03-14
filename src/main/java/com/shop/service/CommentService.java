package com.shop.service;

import org.springframework.http.ResponseEntity;

import com.shop.dto.user.inquiry.CommentCreateRequest;

// 관리자 답변 비즈니스 로직 인터페이스
public interface CommentService {

    // 답변 작성 (관리자용) - 답변 작성 후 게시글 상태를 '답변완료'로 자동 변경
    ResponseEntity<?> createComment(CommentCreateRequest request);

    // 답변 삭제 (관리자용)
    ResponseEntity<?> deleteComment(Long commentNo);
}
