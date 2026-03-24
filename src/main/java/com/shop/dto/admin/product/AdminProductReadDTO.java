package com.shop.dto.admin.product;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

//상품 상세 DTO (상품 기본정보 + 이미지 + 옵션)
@Data
public class AdminProductReadDTO {
	//상품 기본정보
	private Long productNo;
    private String name;
    private Long price;
    private Integer discountRate;
    private Long salePrice;
    private Long categoryId;
    private String categoryName;
    private String description;
    private String useYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private String sameDayDeliveryYn;
    //이미지 정보
    private List<AdminProductImageDTO> images;
    //옵션 정보
    private List<AdminProductOptionDTO> options;
}
