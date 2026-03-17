package com.shop.dto.user.inquiry;

import lombok.Getter;
import lombok.Setter;

// =========================================
// FAQ 페이징 요청 DTO
// 컨트롤러에서 쿼리 파라미터로 받아서 Mapper에 전달
// =========================================
@Getter
@Setter
public class FaqPageRequest {

    // 현재 페이지 번호 (1부터 시작, 기본값 1)
    private int page = 1;

    // 페이지당 표시 개수 (기본값 10)
    private int size = 10;

    // 카테고리 필터 (null 또는 "전체"면 전체 조회)
    private String category;

    // 키워드 검색 (null 또는 빈 문자열이면 검색 안 함)
    private String keyword;

    // =========================================
    // Oracle ROWNUM 페이징용 계산 메서드
    // startRow: 조회 시작 행 번호 (0-based)
    // endRow: 조회 끝 행 번호
    // =========================================
    public int getStartRow() {
        return (page - 1) * size;
    }

    public int getEndRow() {
        return page * size;
    }
}
