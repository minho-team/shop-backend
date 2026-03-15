package com.shop.service.admin.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.dto.admin.product.AdminProductDetailDTO;
import com.shop.dto.admin.product.AdminProductImageDTO;
import com.shop.dto.admin.product.AdminProductListDTO;
import com.shop.dto.admin.product.AdminProductOptionDTO;
import com.shop.dto.admin.product.AdminProductPageResponseDTO;
import com.shop.dto.admin.product.AdminProductReadDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;
import com.shop.mapper.AdminProductMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService{
	
	private final AdminProductMapper adminProductMapper;
	
	@Override
	public AdminProductPageResponseDTO getProductList(AdminProductSearchDTO searchDTO) {
		// page 기본값 보정
        if (searchDTO.getPage() <= 0) {
            searchDTO.setPage(1);
        }
        // size 기본값 보정
        if (searchDTO.getSize() <= 0) {
            searchDTO.setSize(10);
        }
        
        // 상품 목록 조회
        List<AdminProductListDTO> list = adminProductMapper.getProductList(searchDTO);
        
        // 전체 개수 조회
        int totalCount = adminProductMapper.getProductCount(searchDTO);
        
        // 전체 페이지 수 계산
        int totalPage = (int) Math.ceil((double) totalCount / searchDTO.getSize());
        
        // 응답 DTO 생성
        AdminProductPageResponseDTO responseDTO = new AdminProductPageResponseDTO();
        responseDTO.setList(list);
        responseDTO.setPage(searchDTO.getPage());
        responseDTO.setSize(searchDTO.getSize());
        responseDTO.setTotalCount(totalCount);
        responseDTO.setTotalPage(totalPage);
        
        return responseDTO;
	}

	@Override
	public AdminProductReadDTO getProduct(Long productNo) {
		// 상품 기본 정보 조회
		AdminProductDetailDTO product = adminProductMapper.getProduct(productNo);
		// 상품 이미지 정보 조회
        List<AdminProductImageDTO> images = adminProductMapper.getProductImages(productNo);
        // 상품 옵션 정보 조회
        List<AdminProductOptionDTO> options = adminProductMapper.getProductOptions(productNo);

        // 응답 DTO 생성
        AdminProductReadDTO responseDTO = new AdminProductReadDTO();

        responseDTO.setProductNo(product.getProductNo());
        responseDTO.setName(product.getName());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setDiscountRate(product.getDiscountRate());
        responseDTO.setSalePrice(product.getSalePrice());
        responseDTO.setCategoryId(product.getCategoryId());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setUseYn(product.getUseYn());
        responseDTO.setSameDayDeliveryYn(product.getSameDayDeliveryYn());
        responseDTO.setViewCount(product.getViewCount());
        responseDTO.setCreatedAt(product.getCreatedAt());
        responseDTO.setUpdatedAt(product.getUpdatedAt());

        responseDTO.setImages(images);
        responseDTO.setOptions(options);
        
        return responseDTO;
	}

}
