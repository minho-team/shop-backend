package com.shop.dto.admin.product;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

//상품 등록 DTO
@Data
public class AdminProductInsertDTO {
    
    private Long productNo;

    private String name;
    private Long categoryId;
    private Integer price;
    private Integer discountRate;
    private String description;
    private String useYn;
    private String sameDayDeliveryYn;
    
    // 썸네일 이미지 NOT NULL
    private MultipartFile thumbImage;
    // 대표 이미지 1장
    private MultipartFile mainImage;
    // 갤러리 이미지 여러 장
    private List<MultipartFile> galleryImages;
    
}
