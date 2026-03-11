package com.shop.mapper;

import java.util.List;
import com.shop.domain.Board;
import com.shop.dto.BoardCreateRequest;
import com.shop.dto.UpdateBoardRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 1:1 문의 게시판 DB 접근을 담당하는 MyBatis Mapper
@Mapper
public interface BoardMapper {

    // 게시글 작성
    void createBoard(BoardCreateRequest request) throws Exception;

    // 게시글 전체 조회 (삭제되지 않은 게시글, 최신순) - 관리자용
    List<Board> readAllBoard() throws Exception;

    // 내 문의 내역 조회 (로그인한 회원의 게시글만, 최신순)
    List<Board> readMyBoard(Long memberNo) throws Exception;

    // 게시글 하나 조회
    Board readOneBoard(Long boardNo) throws Exception;

    // 조회수 1 증가
    void increaseViewCount(Long boardNo) throws Exception;

    // 게시글 수정 - 파라미터 2개라 @Param 필요
    void updateBoard(@Param("boardNo") Long boardNo, @Param("dto") UpdateBoardRequest dto) throws Exception;

    // 게시글 삭제 (soft delete) - 본인 글만 삭제 가능
    void deleteBoard(@Param("boardNo") Long boardNo, @Param("memberNo") Long memberNo) throws Exception;

    // 문의 상태 변경 (관리자가 답변 후 '답변완료'로 변경)
    void updateStatus(@Param("boardNo") Long boardNo, @Param("status") String status) throws Exception;
}
