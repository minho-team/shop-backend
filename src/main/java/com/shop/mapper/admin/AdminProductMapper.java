package com.shop.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.dto.admin.product.AdminProductBasicUpdateDTO;
import com.shop.dto.admin.product.AdminProductDetailDTO;
import com.shop.dto.admin.product.AdminProductImageDTO;
import com.shop.dto.admin.product.AdminProductInsertDTO;
import com.shop.dto.admin.product.AdminProductListDTO;
import com.shop.dto.admin.product.AdminProductOptionDTO;
import com.shop.dto.admin.product.AdminProductOptionRequestDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;

@Mapper
public interface AdminProductMapper {

	// 관리자 상품 목록 조회
	List<AdminProductListDTO> getProductList(AdminProductSearchDTO searchDTO);
	
	// 관리자 상품 총 개수 조회
    int getProductCount(AdminProductSearchDTO searchDTO);
    
    // 상품 기본 상세 조회
    AdminProductDetailDTO getProduct(Long productNo);

    // 상품 이미지 목록 조회
    List<AdminProductImageDTO> getProductImages(Long productNo);

    // 상품 옵션 목록 조회
    List<AdminProductOptionDTO> getProductOptions(Long productNo);
    
    // 상품 등록
    void insertProduct(AdminProductInsertDTO dto);

    // 상품 이미지 등록
    void insertProductImage(@Param("productNo") Long productNo,
                            @Param("imageUrl") String imageUrl,
                            @Param("imageType") String imageType,
                            @Param("sortOrder") int sortOrder);
    // 상품 옵션 등록
    void insertProductOption(AdminProductOptionDTO optionDTO);
    
    // 상품 기본정보 수정
	void updateProductBasic(@Param("productNo")Long productNo, 
							@Param("dto")AdminProductBasicUpdateDTO dto);
	
	// 상품 옵션 조회 (Null 여부 확인용)
	AdminProductOptionDTO getProductOption(@Param("productNo") Long productNo,
            								  @Param("productOptionNo") Long productOptionNo);
	// 상품 옵션 수정
	void updateProductOption(@Param("productNo") Long productNo,
				            @Param("productOptionNo") Long productOptionNo,
				            @Param("dto") AdminProductOptionRequestDTO dto);
	
	// 상품 옵션 삭제
	void deleteProductOption(@Param("productNo") Long productNo,
            					@Param("productOptionNo") Long productOptionNo);
	
	// 상품 use_yn = 'N' 처리
	void softDeleteProduct(Long productNo);

	// 해당 상품 이미지 전체 삭제
	void deleteProductImages(Long productNo);
    
	
}
