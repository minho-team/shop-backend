package com.shop.service.admin.product;

import com.shop.dto.admin.product.AdminProductBasicUpdateDTO;
import com.shop.dto.admin.product.AdminProductInsertDTO;
import com.shop.dto.admin.product.AdminProductOptionRequestDTO;
import com.shop.dto.admin.product.AdminProductPageResponseDTO;
import com.shop.dto.admin.product.AdminProductReadDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;

public interface AdminProductService {
	// 관리자 상품 목록 조회
    AdminProductPageResponseDTO getProductList(AdminProductSearchDTO searchDTO);
    
    // 상세 조회
    AdminProductReadDTO getProduct(Long productNo);

    // 상품 등록
	void insertProduct(AdminProductInsertDTO dto);
	
	// 상품 기본정보 수정
	void updateProductsBasic(Long productNo, AdminProductBasicUpdateDTO dto);
	
	// 상품 옵션 수정
	void updateProductOption(Long productNo, Long productOptionNo, AdminProductOptionRequestDTO dto);

	// 상품 옵션 삭제
	void deleteProductOption(Long productNo, Long productOptionNo);
}
