package com.shop.dto.user.inquiry;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// =========================================
// 페이징 응답 공통 DTO
// 프론트엔드로 리스트 + 페이징 정보를 함께 전달
// =========================================
@Getter
@Setter
public class PageResponse<T> {

    // 현재 페이지 데이터 목록
    private List<T> list;

    // 전체 데이터 수
    private int totalCount;

    // 전체 페이지 수 (totalCount / pageSize 올림)
    private int totalPages;

    // 현재 페이지 번호
    private int currentPage;

    // 페이지당 표시 개수
    private int pageSize;

    // =========================================
    // 생성자 - 목록 + 전체 건수 + 요청 정보로 페이징 계산
    // list: 현재 페이지 데이터
    // totalCount: 전체 건수
    // request: 페이지 요청 정보 (page, size)
    // =========================================
    public PageResponse(List<T> list, int totalCount, int page, int size) {
        this.list = list;
        this.totalCount = totalCount;
        this.currentPage = page;
        this.pageSize = size;
        // 전체 페이지 수 계산 (데이터 없으면 최소 1페이지)
        this.totalPages = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / size);
    }
}
