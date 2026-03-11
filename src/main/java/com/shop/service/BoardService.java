package com.shop.service;

import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// 1:1 문의 게시판 비즈니스 로직 인터페이스
public interface BoardService {

    // 게시글 작성 (첨부파일 포함)
    ResponseEntity<?> createBoard(BoardCreateRequest request, List<MultipartFile> files);

    // 게시글 전체 조회 (관리자용)
    ResponseEntity<?> readAllBoard();

    // 내 문의 내역 조회 (로그인한 회원)
    ResponseEntity<?> readMyBoard(Long memberNo);

    // 게시글 하나 조회 (첨부파일 + 댓글 포함)
    ResponseEntity<?> readOneBoard(Long boardNo);

    // 게시글 수정
    ResponseEntity<?> updateBoard(Long boardNo, UpdateBoardRequest dto);

    // 게시글 삭제 (본인 글만 가능)
    ResponseEntity<?> deleteBoard(Long boardNo, Long memberNo);
}
