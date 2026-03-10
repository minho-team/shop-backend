package com.shop.service.Impl;

import com.shop.domain.Board;
import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;
import com.shop.mapper.BoardMapper;
import com.shop.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

// 게시판 서비스 구현 클래스
@Service
@RequiredArgsConstructor 
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper; 

    // 게시글 작성 처리
    @Override
    public ResponseEntity<?> createBoard(BoardCreateRequest request) {
        try {
            boardMapper.createBoard(request); 
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 전체 조회 처리
    @Override
    public ResponseEntity<?> readAllBoard() {
        try {
            List<Board> list = boardMapper.readAllBoard(); 
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 하나 조회 처리
    @Override
    public ResponseEntity<?> readOneBoard(Long boardNo) {
        try {
            Board board = boardMapper.readOneBoard(boardNo); 
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 수정 처리
    @Override
    public ResponseEntity<?> updateBoard(Long boardNo, UpdateBoardRequest dto) {
        try {
            boardMapper.updateBoard(boardNo, dto); 
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 삭제 처리
    @Override
    public ResponseEntity<?> deleteBoard(Long boardNo, Long memberNo) {
        try {
            boardMapper.deleteBoard(boardNo, memberNo); 
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}