package com.shop.controller;

import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;
import com.shop.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// 1:1 문의 게시판 API를 처리하는 REST 컨트롤러
@RestController

// 게시판 API 기본 경로
@RequestMapping("/api/board")

// final 필드를 사용하는 생성자 자동 생성
@RequiredArgsConstructor
public class BoardController {

    // 게시판 서비스 객체
    private final BoardService boardService;

    // 게시글 작성 API (첨부파일 포함 - multipart/form-data)
    // request: 게시글 정보 JSON, files: 첨부 이미지 (선택)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createBoard(
            @RequestPart("request") BoardCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal User user) {

        // JWT에서 추출한 회원번호를 request에 주입
        request.setMemberNo(Long.parseLong(user.getUsername()));

        return boardService.createBoard(request, files);
    }

    // 게시글 전체 조회 API - 관리자용
    @GetMapping
    public ResponseEntity<?> readAllBoard() {
        return boardService.readAllBoard();
    }

    // 내 문의 내역 조회 API (로그인한 회원의 게시글만)
    @GetMapping("/my")
    public ResponseEntity<?> readMyBoard(@AuthenticationPrincipal User user) {

        // JWT에서 추출한 회원번호로 내 글만 조회
        Long memberNo = Long.parseLong(user.getUsername());

        return boardService.readMyBoard(memberNo);
    }

    // 게시글 하나 조회 API (첨부파일 + 관리자 답변 포함)
    @GetMapping("/{boardNo}")
    public ResponseEntity<?> readOneBoard(@PathVariable Long boardNo) {
        return boardService.readOneBoard(boardNo);
    }

    // 게시글 수정 API
    @PutMapping("/{boardNo}")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long boardNo,
            @RequestBody UpdateBoardRequest dto) {
        return boardService.updateBoard(boardNo, dto);
    }

    // 게시글 삭제 API (본인 글만 삭제 가능)
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<?> deleteBoard(
            @PathVariable Long boardNo,
            @AuthenticationPrincipal User user) {

        // JWT에서 추출한 회원번호로 본인 확인
        Long memberNo = Long.parseLong(user.getUsername());

        return boardService.deleteBoard(boardNo, memberNo);
    }
}
