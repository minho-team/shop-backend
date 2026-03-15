package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.shop.dto.admin.product.AdminProductDetailDTO;
import com.shop.dto.admin.product.AdminProductImageDTO;
import com.shop.dto.admin.product.AdminProductListDTO;
import com.shop.dto.admin.product.AdminProductOptionDTO;
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
}
