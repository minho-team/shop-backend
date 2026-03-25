package com.shop.mapper.admin;

import com.shop.dto.user.inquiry.CommentCreateRequest;
import org.apache.ibatis.annotations.Mapper;

// 관리자 전용 댓글(답변) DB 접근 Mapper
@Mapper
public interface AdminCommentMapper {

    // 답변 작성
    void createComment(CommentCreateRequest request) throws Exception;

    // 답변 삭제 (soft delete)
    void deleteComment(Long commentNo) throws Exception;

    // 답변 번호로 해당 문의 번호 조회 (삭제 후 상태 복구에 사용)
    Long getInquiryNoByCommentNo(Long commentNo) throws Exception;

    // 특정 문의글의 남은 답변 수 조회 (삭제되지 않은 것만)
    int countCommentsByInquiryNo(Long inquiryNo) throws Exception;
}
