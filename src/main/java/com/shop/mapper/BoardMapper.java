package com.shop.mapper;

import java.util.List;
import com.shop.domain.Board;
import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;

// 게시판 DB 접근을 담당하는 MyBatis Mapper
public interface BoardMapper {

    // 게시글 작성
    public void createBoard(BoardCreateRequest request) throws Exception;

    // 게시글 전체 조회
    public List<Board> readAllBoard() throws Exception;

    // 게시글 하나 조회
    public Board readOneBoard(Long boardNo) throws Exception;

    // 게시글 수정
    public void updateBoard(Long boardNo, UpdateBoardRequest dto) throws Exception;

    // 게시글 삭제 (soft delete)
    public void deleteBoard(Long boardNo, Long memberNo) throws Exception;
}