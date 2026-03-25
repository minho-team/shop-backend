package com.shop.mapper.user;

import java.util.List;
import com.shop.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

// 사용자 전용 댓글(답변) DB 접근 Mapper
@Mapper
public interface CommentMapper {

    // 문의글에 달린 답변 목록 조회 (삭제되지 않은 것만, 작성순)
    List<Comment> getCommentsByInquiryNo(Long inquiryNo) throws Exception;
}	