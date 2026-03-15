package com.shop.service.admin.product;

import com.shop.dto.admin.product.AdminProductPageResponseDTO;
import com.shop.dto.admin.product.AdminProductReadDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;

public interface AdminProductService {
	// 관리자 상품 목록 조회
    AdminProductPageResponseDTO getProductList(AdminProductSearchDTO searchDTO);
    
    // 상세 조회
    AdminProductReadDTO getProduct(Long productNo);
}
