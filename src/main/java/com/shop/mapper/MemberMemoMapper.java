package com.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.MemberMemo;

public interface MemberMemoMapper {

    // ================================================
    // 메모 저장
    // ================================================
    void insertMemo(MemberMemo memo) throws Exception;

    // ================================================
    // 특정 회원 메모 전체 조회 (최신순)
    // ================================================
    List<MemberMemo> selectMemosByMemberNo(@Param("memberNo") Long memberNo) throws Exception;

    // ================================================
    // 메모 삭제
    // ================================================
    void deleteMemo(@Param("memoNo") Long memoNo) throws Exception;
}