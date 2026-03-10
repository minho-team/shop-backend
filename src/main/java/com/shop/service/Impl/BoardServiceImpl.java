package com.shop.service.Impl;

import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;
import com.shop.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// 게시판 서비스 구현 클래스
@Service
public class BoardServiceImpl implements BoardService {

    // 게시글 작성 처리
    @Override
    public ResponseEntity<?> createBoard(BoardCreateRequest request) {
        return ResponseEntity.ok().build();
    }

    // 게시글 전체 조회 처리
    @Override
    public ResponseEntity<?> readAllBoard() {
        return ResponseEntity.ok().build();
    }

    // 게시글 하나 조회 처리
    @Override
    public ResponseEntity<?> readOneBoard(Long boardNo) {
        return ResponseEntity.ok().build();
    }

    // 게시글 수정 처리
    @Override
    public ResponseEntity<?> updateBoard(Long boardNo, UpdateBoardRequest dto) {
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제 처리
    @Override
    public ResponseEntity<?> deleteBoard(Long boardNo, Long memberNo) {
        return ResponseEntity.ok().build();
    }
}