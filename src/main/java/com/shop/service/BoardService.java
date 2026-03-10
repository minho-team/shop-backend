package com.shop.service;

import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;
import org.springframework.http.ResponseEntity;

// 게시판 비즈니스 로직 인터페이스
public interface BoardService {

    // 게시글 작성
    ResponseEntity<?> createBoard(BoardCreateRequest request);

    // 게시글 전체 조회
    ResponseEntity<?> readAllBoard();

    // 게시글 하나 조회
    ResponseEntity<?> readOneBoard(Long boardNo);

    // 게시글 수정
    ResponseEntity<?> updateBoard(Long boardNo, UpdateBoardRequest dto);

    // 게시글 삭제
    ResponseEntity<?> deleteBoard(Long boardNo, Long memberNo);
}