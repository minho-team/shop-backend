package com.shop.service.admin.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.dto.admin.product.AdminProductDetailDTO;
import com.shop.dto.admin.product.AdminProductImageDTO;
import com.shop.dto.admin.product.AdminProductInsertDTO;
import com.shop.dto.admin.product.AdminProductListDTO;
import com.shop.dto.admin.product.AdminProductOptionDTO;
import com.shop.dto.admin.product.AdminProductPageResponseDTO;
import com.shop.dto.admin.product.AdminProductReadDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;
import com.shop.mapper.AdminProductMapper;
import com.shop.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService{
	
	private final AdminProductMapper adminProductMapper;
	private final CustomFileUtil customFileUtil;
	
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

	@Override
	@Transactional
	public void insertProduct(AdminProductInsertDTO dto) {
	    // 썸네일 필수 재검사
//	    if (dto.getThumbImage() == null || dto.getThumbImage().isEmpty()) {
//	        throw new IllegalArgumentException("썸네일 이미지는 필수입니다.");
//	    }

	    // 나중에 실패 시 삭제할 파일명을 모아둘 리스트
	    List<String> savedFileNames = new ArrayList<>();

	    try {
	        // 파일 저장
	        String thumbImageName = customFileUtil.saveFile(dto.getThumbImage());
	        String mainImageName = customFileUtil.saveFile(dto.getMainImage());
	        List<String> galleryImageNames = customFileUtil.saveFiles(dto.getGalleryImages());
	        String sizeImageName = customFileUtil.saveFile(dto.getSizeImage());

	        // 삭제 대비용 저장 파일명 수집
	        if (thumbImageName != null) {
	            savedFileNames.add(thumbImageName);
	        }
	        if (mainImageName != null) {
	            savedFileNames.add(mainImageName);
	        }
	        if (galleryImageNames != null && !galleryImageNames.isEmpty()) {
	            savedFileNames.addAll(galleryImageNames);
	        }
	        if (sizeImageName != null) {
	            savedFileNames.add(sizeImageName);
	        }

	        // 상품 기본 정보 저장
	        adminProductMapper.insertProduct(dto);

	        // 방금 저장된 상품 번호 꺼내기
	        Long productNo = dto.getProductNo();

	        if (productNo == null) {
	            throw new IllegalStateException("상품 번호 생성에 실패했습니다.");
	        }

	        // 이미지 정보 저장
	        insertProductImage(productNo, thumbImageName, "THUMB", 1);

	        if (mainImageName != null) {
	            insertProductImage(productNo, mainImageName, "MAIN", 1);
	        }

	        if (galleryImageNames != null && !galleryImageNames.isEmpty()) {
	            for (int i = 0; i < galleryImageNames.size(); i++) {
	                insertProductImage(productNo, galleryImageNames.get(i), "GALLERY", i + 1);
	            }
	        }

	        if (sizeImageName != null) {
	            insertProductImage(productNo, sizeImageName, "SIZE", 1);
	        }

	        // 옵션 저장
	        if (dto.getOptions() != null && !dto.getOptions().isEmpty()) {
	            for (AdminProductOptionDTO optionDTO : dto.getOptions()) {
	                optionDTO.setProductNo(productNo);
	                adminProductMapper.insertProductOption(optionDTO);
	            }
	        }

	    } catch (Exception e) {
	        log.error("상품 등록 서비스 처리 중 오류", e);

	        // 실패 시 이미 저장한 파일 삭제
	        if (!savedFileNames.isEmpty()) {
	            customFileUtil.deleteFiles(savedFileNames);
	        }

	        throw new RuntimeException("상품 등록 처리 중 오류가 발생했습니다.", e);
	    }
	}

	private void insertProductImage(Long productNo, String imageUrl, String imageType, int sortOrder) {
	    if (imageUrl == null || imageUrl.trim().isEmpty()) {
	        return;
	    }

	    adminProductMapper.insertProductImage(productNo, imageUrl, imageType, sortOrder);
	}

	
}
