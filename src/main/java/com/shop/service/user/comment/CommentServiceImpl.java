package com.shop.service.user.comment;

import com.shop.dto.user.inquiry.CommentCreateRequest;
import com.shop.mapper.user.CommentMapper;
import com.shop.mapper.user.InquiryMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// 관리자 답변 서비스 구현 클래스
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    // Mapper 주입
    private final CommentMapper commentMapper;
    private final InquiryMapper inquiryMapper;

    // 답변 작성 처리 (답변 저장 후 게시글 상태를 '답변완료'로 자동 변경)
    @Override
    public ResponseEntity<?> createComment(CommentCreateRequest request) {
        try {
            // 1. 답변 저장
            commentMapper.createComment(request);

            // 2. 해당 게시글 상태를 '답변완료'로 변경
            inquiryMapper.updateStatus(request.getInquiryNo(), "답변완료");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("답변 작성 실패: " + e.getMessage());
        }
    }

    // 답변 삭제 처리 (soft delete)
    // 삭제 후 해당 문의글에 남은 답변이 없으면 상태를 '답변대기'로 복구
    @Override
    public ResponseEntity<?> deleteComment(Long commentNo) {
        try {
            // 1. 삭제 전 해당 답변의 문의 번호 조회 (상태 복구에 사용)
            Long inquiryNo = commentMapper.getInquiryNoByCommentNo(commentNo);

            // 2. 답변 삭제 (soft delete)
            commentMapper.deleteComment(commentNo);

            // 3. 해당 문의글의 남은 답변 수 확인
            if (inquiryNo != null) {
                int remainCount = commentMapper.countCommentsByInquiryNo(inquiryNo);

                // 4. 남은 답변이 없으면 문의 상태를 '답변대기'로 복구
                if (remainCount == 0) {
                    inquiryMapper.updateStatus(inquiryNo, "답변대기");
                }
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("답변 삭제 실패: " + e.getMessage());
        }
    }
}