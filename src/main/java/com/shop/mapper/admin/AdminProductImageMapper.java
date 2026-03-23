package com.shop.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.dto.admin.product.AdminProductImageDTO;

@Mapper
public interface AdminProductImageMapper {

    // 타입별 단일 이미지 조회
    AdminProductImageDTO getSingleImageByType(@Param("productNo") Long productNo,
                                              @Param("imageType") String imageType);

    // 갤러리 이미지 목록 조회
    List<AdminProductImageDTO> getGalleryImages(Long productNo);

    // 갤러리 이미지 단건 조회
    AdminProductImageDTO getGalleryImage(@Param("productNo") Long productNo,
                                         @Param("productImgNo") Long productImgNo);

    // 이미지 등록
    void insertProductImage(AdminProductImageDTO dto);

    // 이미지 경로 수정
    void updateImageUrl(AdminProductImageDTO dto);

    // 타입별 단일 이미지 삭제
    void deleteSingleImageByType(@Param("productNo") Long productNo,
                                 @Param("imageType") String imageType);

    // 갤러리 이미지 삭제
    void deleteGalleryImage(@Param("productNo") Long productNo,
                            @Param("productImgNo") Long productImgNo);

    // 다음 갤러리 정렬순서 조회
    Integer getNextGallerySortOrder(Long productNo);
}