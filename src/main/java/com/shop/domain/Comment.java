package com.shop.domain;

import lombok.Data;
import java.sql.Timestamp;

// 관리자 답변(댓글) 테이블 데이터를 담는 도메인 클래스
@Data
public class Comment {

    // 댓글 번호 (PK) - [수정] commentNo → inquiryCommentNo (XML alias와 일치)
    private Long inquiryCommentNo;

    // 게시글 번호 (FK → inquiry.inquiry_no)
    private Long inquiryNo;

    // 작성자 회원 번호 (FK, 관리자)
    private Long memberNo;

    // 답변 내용
    private String content;

    // 작성일
    private Timestamp createdAt;

    // 수정일
    private Timestamp updatedAt;

    // 삭제 여부 (N: 정상 / Y: 삭제)
    private String deleteYn;
}