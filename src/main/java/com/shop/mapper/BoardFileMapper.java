package com.shop.mapper;

import java.util.List;
import com.shop.domain.BoardFile;
import org.apache.ibatis.annotations.Mapper;

// 게시글 첨부파일 DB 접근을 담당하는 MyBatis Mapper
@Mapper
public interface BoardFileMapper {

    // 첨부파일 저장
    void insertFile(BoardFile boardFile) throws Exception;

    // 게시글에 해당하는 첨부파일 목록 조회 (삭제되지 않은 것만)
    List<BoardFile> getFilesByBoardNo(Long boardNo) throws Exception;

    // 첨부파일 삭제 (soft delete)
    void deleteFile(Long fileNo) throws Exception;
}
