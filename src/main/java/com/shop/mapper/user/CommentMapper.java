package com.shop.mapper.user;

import java.util.List;
import com.shop.domain.Comment;
import com.shop.dto.user.inquiry.CommentCreateRequest;

import org.apache.ibatis.annotations.Mapper;

// 관리자 답변(댓글) DB 접근을 담당하는 MyBatis Mapper
@Mapper
public interface CommentMapper {

    // 답변 작성
    void createComment(CommentCreateRequest request) throws Exception;

    // 문의글에 달린 답변 목록 조회 (삭제되지 않은 것만, 작성순)
    List<Comment> getCommentsByInquiryNo(Long inquiryNo) throws Exception;

    // 답변 삭제 (soft delete)
    void deleteComment(Long commentNo) throws Exception;

    // 답변 번호로 해당 문의 번호 조회 (삭제 후 상태 복구에 사용)
    Long getInquiryNoByCommentNo(Long commentNo) throws Exception;

    // 특정 문의글의 남은 답변 수 조회 (삭제되지 않은 것만)
    int countCommentsByInquiryNo(Long inquiryNo) throws Exception;
}