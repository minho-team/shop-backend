package com.shop.domain;

import lombok.Data;
import java.sql.Timestamp;

// 게시글 첨부파일 테이블 데이터를 담는 도메인 클래스
@Data
public class BoardFile {

    // 파일 번호 (PK)
    private Long fileNo;

    // 게시글 번호 (FK → board.board_no)
    private Long boardNo;

    // 파일 저장 경로 (서버 내 접근 URL)
    private String fileUrl;

    // 원본 파일명
    private String fileName;

    // 파일 크기 (byte)
    private Long fileSize;

    // 파일 타입 (image/jpeg, image/png 등)
    private String fileType;

    // 업로드일
    private Timestamp createdAt;

    // 삭제 여부 (N: 정상 / Y: 삭제)
    private String deleteYn;
}
