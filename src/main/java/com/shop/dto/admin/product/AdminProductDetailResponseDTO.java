package com.shop.dto.admin.product;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

// 상품 상세 응답 DTO
@Data
public class AdminProductDetailResponseDTO {
	
	private Long productNo;
    private Long categoryNo;
    private String categoryName;
    private String productName;
    private Long price;
    private Integer discountRate;
    private String useYn;
    private String sameDayDeliveryYn;
    private Integer viewCount;
    private LocalDateTime createdAt;

    private AdminProductImageDTO thumbnailImage;
    private AdminProductImageDTO mainImage;
    private List<AdminProductImageDTO> galleryImages;
    private AdminProductImageDTO sizeChartImage;

    private List<AdminProductOptionDTO> options;
}
