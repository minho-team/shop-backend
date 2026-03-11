package com.shop.service.Impl;

import com.shop.dto.CommentCreateRequest;
import com.shop.mapper.BoardMapper;
import com.shop.mapper.CommentMapper;
import com.shop.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// 관리자 답변 서비스 구현 클래스
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    // Mapper 주입
    private final CommentMapper commentMapper;
    private final BoardMapper   boardMapper;

    // 답변 작성 처리 (답변 저장 후 게시글 상태를 '답변완료'로 자동 변경)
    @Override
    public ResponseEntity<?> createComment(CommentCreateRequest request) {
        try {
            // 1. 답변 저장
            commentMapper.createComment(request);

            // 2. 해당 게시글 상태를 '답변완료'로 변경
            boardMapper.updateStatus(request.getBoardNo(), "답변완료");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("답변 작성 실패: " + e.getMessage());
        }
    }

    // 답변 삭제 처리 (soft delete)
    @Override
    public ResponseEntity<?> deleteComment(Long commentNo) {
        try {
            commentMapper.deleteComment(commentNo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("답변 삭제 실패: " + e.getMessage());
        }
    }
}
