package com.shop.service.admin.product;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface AdminProductImageService {

    // 상품 이미지 조회
    Map<String, Object> getProductImages(Long productNo);

    // 썸네일 이미지 변경
    void updateThumbImage(Long productNo, MultipartFile file);

    // 메인 이미지 변경
    void updateMainImage(Long productNo, MultipartFile file);

    // 메인 이미지 삭제
    void deleteMainImage(Long productNo);

    // 갤러리 이미지 추가
    void addGalleryImage(Long productNo, MultipartFile file);

    // 갤러리 이미지 변경
    void updateGalleryImage(Long productNo, Long productImgNo, MultipartFile file);

    // 갤러리 이미지 삭제
    void deleteGalleryImage(Long productNo, Long productImgNo);

    // 사이즈표 이미지 변경
    void updateSizeImage(Long productNo, MultipartFile file);

    // 사이즈표 이미지 삭제
    void deleteSizeImage(Long productNo);
}