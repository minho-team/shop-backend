package com.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import lombok.RequiredArgsConstructor;
import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;
import com.shop.service.BoardService;

// 게시판 관련 API를 처리하는 REST 컨트롤러
@RestController

// 게시판 API 기본 경로 설정
@RequestMapping("/api/board")

// final 필드를 사용하는 생성자 자동 생성
@RequiredArgsConstructor
public class BoardController {

    // 게시판 서비스 객체
    private final BoardService boardService;

    // 게시글 작성 API
    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody BoardCreateRequest request) {
        return boardService.createBoard(request);
    }

    // 게시글 전체 조회 API
    @GetMapping
    public ResponseEntity<?> readAllBoard() {
        return boardService.readAllBoard();
    }

    // 게시글 하나 조회 API
    @GetMapping("/{boardNo}")
    public ResponseEntity<?> readOneBoard(@PathVariable Long boardNo) {
        return boardService.readOneBoard(boardNo);
    }

    // 게시글 수정 API
    @PutMapping("/{boardNo}")
    public ResponseEntity<?> updateBoard(@PathVariable Long boardNo, @RequestBody UpdateBoardRequest dto) {
        return boardService.updateBoard(boardNo, dto);
    }

    // 게시글 삭제 API (로그인 사용자 정보 사용)
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardNo, @AuthenticationPrincipal User user) {

        // 로그인 사용자 username을 memberNo로 변환
        Long memberNo = Long.parseLong(user.getUsername());

        return boardService.deleteBoard(boardNo, memberNo);
    }
}