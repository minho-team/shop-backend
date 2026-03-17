package com.shop.dto.admin.product;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//상품 등록 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminProductInsertDTO {
    
	private Long productNo;
	
    private String name;
    private Long categoryId;
    private Long price;
    private Integer discountRate;
    private String description;
    private String useYn;
    private String sameDayDeliveryYn;
    
    // 썸네일 이미지 (필수)
    private MultipartFile thumbImage;
    // 대표 이미지 (선택)
    private MultipartFile mainImage;
    // 갤러리 이미지 (선택, 여러장)
    private List<MultipartFile> galleryImages;
    // 사이즈표 이미지 (선택)
    private MultipartFile sizeImage;
    // 옵션
    private List<AdminProductOptionDTO> options;
}
