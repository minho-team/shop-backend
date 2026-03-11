package com.shop.domain;

import lombok.Data;
import java.sql.Timestamp;

// 관리자 답변(댓글) 테이블 데이터를 담는 도메인 클래스
@Data
public class Comment {

    // 댓글 번호 (PK)
    private Long commentNo;

    // 게시글 번호 (FK → board.board_no)
    private Long boardNo;

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
