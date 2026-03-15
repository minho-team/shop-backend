package com.shop.dto.admin.product;

import lombok.Data;

//상품 검색 DTO
@Data
public class AdminProductSearchDTO {
    private String keyword;
    private Long categoryId;
    private String useYn;
    private String sameDayDeliveryYn;
    
    //API 페이징 정보
    private int page = 1;
    private int size = 10;
}
