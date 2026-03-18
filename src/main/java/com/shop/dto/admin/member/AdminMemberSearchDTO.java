package com.shop.dto.admin.member;

import lombok.Data;

// ================================================
// 관리자 회원 목록 조회용 검색 조건 DTO
// page, size, status, keyword를 받아서
// startRow, endRow를 계산한 뒤 Mapper에 전달
// ================================================
@Data
public class AdminMemberSearchDTO {

    // 현재 페이지 번호 (기본값 1)
    private int page = 1;

    // 페이지당 표시 수 (기본값 5 - 항상 5개씩)
    private int size = 5;

    // 상태 필터 (ACTIVE / DORMANT / SUSPENDED / null = 전체)
    private String status;

    // 검색 키워드 (memberId 또는 name으로 검색)
    private String keyword;

    // 페이징 계산값 - 서비스에서 세팅해줌
    // 예) 1페이지: startRow=1, endRow=5
    // 예) 2페이지: startRow=6, endRow=10
    private int startRow;
    private int endRow;
}