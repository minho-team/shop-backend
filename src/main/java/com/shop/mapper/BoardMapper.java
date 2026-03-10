package com.shop.mapper;

import java.util.List;
import com.shop.domain.Board;
import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 게시판 DB 접근을 담당하는 MyBatis Mapper
@Mapper 
public interface BoardMapper {

    // 게시글 작성
    public void createBoard(BoardCreateRequest request) throws Exception;

    // 게시글 전체 조회
    public List<Board> readAllBoard() throws Exception;

    // 게시글 하나 조회
    public Board readOneBoard(Long boardNo) throws Exception;

    // 게시글 수정 - 파라미터 2개라 @Param 필요 ✅
    public void updateBoard(@Param("boardNo") Long boardNo, @Param("dto") UpdateBoardRequest dto) throws Exception;

    // 게시글 삭제 (soft delete) - 파라미터 2개라 @Param 필요 ✅
    public void deleteBoard(@Param("boardNo") Long boardNo, @Param("memberNo") Long memberNo) throws Exception;
}