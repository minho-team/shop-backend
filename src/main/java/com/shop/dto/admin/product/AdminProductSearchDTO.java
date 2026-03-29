package com.shop.dto.admin.product;

import lombok.Data;

//상품 검색 DTO
@Data
public class AdminProductSearchDTO {
    
	// 검색 키워드
	private String keyword;
    
	// 카테고리 1차 검색 (성별)
    private Long genderCategoryId;
    // 카테고리 2차 검색 (대분류)
    private Long mainCategoryId;
    // 카테고리 3차 검색 (소분류)
    private Long categoryId;
    // 실제 검색 기준으로 사용할 category_id
    private Long searchCategoryId;
    
    private String useYn;
    private String sameDayDeliveryYn;
    
    //API 페이징 정보
    private int page = 1;
    private int size = 10;
    
    // 정렬 컬럼
    private String sortBy = "createdAt";

    // 정렬 방향
    private String sortDirection = "desc";
}
