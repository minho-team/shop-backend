package com.shop.domain;

import lombok.Data;
import java.sql.Timestamp;

// 게시판 테이블 데이터를 담는 도메인 클래스
@Data
public class Board {

    // 게시글 번호
    private Long boardNo;

    // 게시글 제목
    private String title;

    // 게시글 내용
    private String content;

    // 작성자 회원 번호
    private Long memberNo;

    // 게시글 생성 시간
    private Timestamp createdAt;

    // 게시글 수정 시간
    private Timestamp updatedAt;

    // 게시글 조회수
    private Integer viewCount;

    // 게시글 삭제 여부 Y : 정상 / N : 삭제
    private String deleteYn;
}