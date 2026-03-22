package com.shop.service.admin.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.dto.admin.product.AdminProductBasicUpdateDTO;
import com.shop.dto.admin.product.AdminProductDetailDTO;
import com.shop.dto.admin.product.AdminProductImageDTO;
import com.shop.dto.admin.product.AdminProductInsertDTO;
import com.shop.dto.admin.product.AdminProductListDTO;
import com.shop.dto.admin.product.AdminProductOptionDTO;
import com.shop.dto.admin.product.AdminProductOptionRequestDTO;
import com.shop.dto.admin.product.AdminProductPageResponseDTO;
import com.shop.dto.admin.product.AdminProductReadDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;
import com.shop.mapper.admin.AdminProductMapper;
import com.shop.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService{
	
	private final AdminProductMapper adminProductMapper;
	private final CustomFileUtil customFileUtil;
	
	// 관리자 상품 목록 조회
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
        
        /* 카테고리 검색 범위 결정 조건 (검색 조건은 좁은 범위 우선 => 넓은 범위 순서로 코딩)
         * 가장 마지막으로 선택된 카테고리를 실제 검색 기준으로 사용
         */
        // 소분류 설정 변경이 있는 경우
        if (searchDTO.getCategoryId() != null && !searchDTO.getCategoryId().toString().isBlank()) {
            searchDTO.setSearchCategoryId(searchDTO.getCategoryId());
        // 대분류 설정 변경이 있는 경우
        } else if (searchDTO.getMainCategoryId() != null) {
            searchDTO.setSearchCategoryId(searchDTO.getMainCategoryId());
        // 성별 설정 변경이 있는 경우
        } else if (searchDTO.getGenderCategoryId() != null) {
            searchDTO.setSearchCategoryId(searchDTO.getGenderCategoryId());
        } else {
            searchDTO.setSearchCategoryId(null);
        }
        
        // 상품 목록 조회
        List<AdminProductListDTO> list = adminProductMapper.getProductList(searchDTO);
        
        // 시드 썸네일 경로 보정
        if (list != null) {
            for (AdminProductListDTO dto : list) {
                String fileName = dto.getThumbnailUrl();

                if (fileName != null && !fileName.isBlank() && !fileName.startsWith("/upload/")) {
                    dto.setThumbnailUrl("/upload/" + fileName);
                }
            }
        }
        
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
	
	// 상품 상세 조회
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
	
	// 상품 등록
	@Override
	@Transactional
	public void insertProduct(AdminProductInsertDTO dto) {

	    List<String> savedFileNames = new ArrayList<>();

	    try {
	        log.info("서비스 진입 - product dto: name={}, categoryId={}, price={}, discountRate={}, useYn={}, sameDayDeliveryYn={}",
	                dto.getName(), dto.getCategoryId(), dto.getPrice(), dto.getDiscountRate(),
	                dto.getUseYn(), dto.getSameDayDeliveryYn());
	        
	        // 유효성 검사
	        if (dto.getThumbImage() == null || dto.getThumbImage().isEmpty()) {
	            throw new IllegalArgumentException("썸네일 이미지는 필수입니다.");
	        }

	        if (dto.getName() == null || dto.getName().isBlank()) {
	            throw new IllegalArgumentException("상품명은 필수입니다.");
	        }

	        if (dto.getCategoryId() == null) {
	            throw new IllegalArgumentException("카테고리는 필수입니다.");
	        }

	        if (dto.getPrice() == null) {
	            throw new IllegalArgumentException("가격은 필수입니다.");
	        }

	        if (dto.getUseYn() == null || dto.getUseYn().isBlank()) {
	            dto.setUseYn("Y");
	        }

	        if (dto.getSameDayDeliveryYn() == null || dto.getSameDayDeliveryYn().isBlank()) {
	            dto.setSameDayDeliveryYn("N");
	        }

	        // 파일 저장
	        String thumbImageName = customFileUtil.saveFile(dto.getThumbImage());
	        String mainImageName = customFileUtil.saveFile(dto.getMainImage());
	        List<String> galleryImageNames = customFileUtil.saveFiles(dto.getGalleryImages());
	        String sizeImageName = customFileUtil.saveFile(dto.getSizeImage());

	        if (thumbImageName != null) savedFileNames.add(thumbImageName);
	        if (mainImageName != null) savedFileNames.add(mainImageName);
	        if (galleryImageNames != null && !galleryImageNames.isEmpty()) {
	            savedFileNames.addAll(galleryImageNames);
	        }
	        if (sizeImageName != null) savedFileNames.add(sizeImageName);

	        log.info("DB insert 직전 - dto productNo={}, name={}, price={}, categoryId={}",
	                dto.getProductNo(), dto.getName(), dto.getPrice(), dto.getCategoryId());

	        // 상품 기본 정보 저장
	        adminProductMapper.insertProduct(dto);

	        Long productNo = dto.getProductNo();

	        if (productNo == null) {
	            throw new IllegalStateException("상품 번호 생성에 실패했습니다.");
	        }

	        // 이미지 저장
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

	                if (optionDTO.getUseYn() == null || optionDTO.getUseYn().isBlank()) {
	                    optionDTO.setUseYn("Y");
	                }

	                adminProductMapper.insertProductOption(optionDTO);
	            }
	        }

	    } catch (Exception e) {
	        log.error("상품 등록 서비스 처리 중 오류", e);

	        if (!savedFileNames.isEmpty()) {
	            customFileUtil.deleteFiles(savedFileNames);
	        }

	        throw new RuntimeException("상품 등록 처리 중 오류가 발생했습니다.", e);
	    }
	}
	
	// 상품 기본정보 수정
	@Override
	@Transactional
	public void updateProductsBasic(Long productNo, AdminProductBasicUpdateDTO dto) {
		
		// 유효성 검사
		if (productNo == null) {
	        throw new IllegalArgumentException("상품번호가 없습니다.");
	    }

	    if (dto.getProductName() == null || dto.getProductName().trim().isEmpty()) {
	        throw new IllegalArgumentException("상품명은 필수입니다.");
	    }

	    if (dto.getPrice() == null || dto.getPrice() < 0) {
	        throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
	    }

	    if (dto.getDiscountRate() == null || dto.getDiscountRate() < 0 || dto.getDiscountRate() > 100) {
	        throw new IllegalArgumentException("할인율은 0~100 사이여야 합니다.");
	    }

	    if (!"Y".equals(dto.getUseYn()) && !"N".equals(dto.getUseYn())) {
	        throw new IllegalArgumentException("판매여부(useYn)는 Y 또는 N만 가능합니다.");
	    }

	    if (!"Y".equals(dto.getSameDayDeliveryYn()) && !"N".equals(dto.getSameDayDeliveryYn())) {
	        throw new IllegalArgumentException("당일배송여부는 Y 또는 N만 가능합니다.");
	    }

	    AdminProductDetailDTO product = adminProductMapper.getProduct(productNo);
	    
	    if (product == null) {
	        throw new IllegalArgumentException("존재하지 않는 상품입니다.");
	    }

	    adminProductMapper.updateProductBasic(productNo, dto);
		
	}
	
	// 상품 옵션 수정
	@Override
	@Transactional
	public void updateProductOption(Long productNo, Long productOptionNo, AdminProductOptionRequestDTO dto) {
		
		// 유효성 검사
		if (productNo == null) {
	        throw new IllegalArgumentException("상품번호가 없습니다.");
	    }

	    if (productOptionNo == null) {
	        throw new IllegalArgumentException("옵션번호가 없습니다.");
	    }

	    if (dto.getColor() == null || dto.getColor().trim().isEmpty()) {
	        throw new IllegalArgumentException("색상은 필수입니다.");
	    }

	    if (dto.getOptionSize() == null || dto.getOptionSize().trim().isEmpty()) {
	        throw new IllegalArgumentException("사이즈는 필수입니다.");
	    }

	    if (dto.getStock() == null || dto.getStock() < 0) {
	        throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
	    }

	    if (!"Y".equals(dto.getUseYn()) && !"N".equals(dto.getUseYn())) {
	        throw new IllegalArgumentException("사용여부는 Y 또는 N만 가능합니다.");
	    }
	    
	    // NULL 옵션 방어
	    AdminProductOptionDTO option = adminProductMapper.getProductOption(productNo, productOptionNo);
	    if (option == null) {
	        throw new IllegalArgumentException("존재하지 않는 옵션입니다.");
	    }

	    adminProductMapper.updateProductOption(productNo, productOptionNo, dto);	
		
	}
	
	// 상품 옵션 추가
	@Override
	@Transactional
	public void insertProductOption(Long productNo, AdminProductOptionRequestDTO dto) {
		
		// 유효성 검사
	    if (productNo == null) {
	        throw new IllegalArgumentException("상품번호가 없습니다.");
	    }

	    if (dto.getColor() == null || dto.getColor().trim().isEmpty()) {
	        throw new IllegalArgumentException("색상은 필수입니다.");
	    }

	    if (dto.getOptionSize() == null || dto.getOptionSize().trim().isEmpty()) {
	        throw new IllegalArgumentException("사이즈는 필수입니다.");
	    }

	    if (dto.getStock() == null || dto.getStock() < 0) {
	        throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
	    }

	    if (!"Y".equals(dto.getUseYn()) && !"N".equals(dto.getUseYn())) {
	        throw new IllegalArgumentException("사용여부는 Y 또는 N만 가능합니다.");
	    }
	    
	    // 해당 상품 존재 유무 확인
	    AdminProductDetailDTO product = adminProductMapper.getProduct(productNo);
	    if (product == null) {
	        throw new IllegalArgumentException("존재하지 않는 상품입니다.");
	    }

	    AdminProductOptionDTO optionDTO = new AdminProductOptionDTO();
	    optionDTO.setProductNo(productNo);
	    optionDTO.setColor(dto.getColor());
	    optionDTO.setOptionSize(dto.getOptionSize());
	    optionDTO.setStock(dto.getStock());
	    optionDTO.setUseYn(dto.getUseYn());

	    adminProductMapper.insertProductOption(optionDTO);
	}
	
	// 상품 옵션 삭제
	@Override
	@Transactional
	public void deleteProductOption(Long productNo, Long productOptionNo) {
		
		// 유효성 검사
	    if (productNo == null) {
	        throw new IllegalArgumentException("상품번호가 없습니다.");
	    }

	    if (productOptionNo == null) {
	        throw new IllegalArgumentException("옵션번호가 없습니다.");
	    }

	    // NULL 옵션 방어
	    AdminProductOptionDTO option = adminProductMapper.getProductOption(productNo, productOptionNo);
	    if (option == null) {
	        throw new IllegalArgumentException("존재하지 않는 옵션입니다.");
	    }

	    adminProductMapper.deleteProductOption(productNo, productOptionNo);
		
	}
	 
	/*
	 * 상품 삭제 (상품은 소프트 삭제, 이미지는 하드 삭제)
	 * 삭제 메서드 순서 중요!!
	 * 이미지 조회 → 파일 삭제 → DB 이미지 삭제 → 상품 soft delete
	 * DB를 먼저 삭제하고 실제 파일을 삭제하려고하면 삭제하려는 파일을 찾을수 없음
	 */
	@Override
	@Transactional
	public void deleteProduct(Long productNo) {

	    // 상품번호 검증
	    if (productNo == null) {
	        throw new IllegalArgumentException("상품번호가 없습니다.");
	    }

	    // 상품 존재 여부 확인
	    AdminProductDetailDTO product = adminProductMapper.getProduct(productNo);
	    if (product == null) {
	        throw new IllegalArgumentException("존재하지 않는 상품입니다.");
	    }

	    // 이미 삭제된 상품인지 확인
	    if ("N".equals(product.getUseYn())) {
	        throw new IllegalArgumentException("이미 삭제된 상품입니다.");
	    }

	    // 해당 상품 이미지 목록 조회
	    List<AdminProductImageDTO> imageList = adminProductMapper.getProductImages(productNo);

	    // 실제 파일 삭제용 파일명 수집
	    List<String> fileNames = new ArrayList<>();

	    if (imageList != null && !imageList.isEmpty()) {
	        for (AdminProductImageDTO image : imageList) {
	            if (image.getImageUrl() != null && !image.getImageUrl().trim().isEmpty()) {
	                fileNames.add(image.getImageUrl());
	            }
	        }
	    }

	    // 실제 업로드 파일 삭제
	    if (!fileNames.isEmpty()) {
	        customFileUtil.deleteFiles(fileNames);
	    }

	    // product_img 테이블 이미지 데이터 하드 삭제
	    adminProductMapper.deleteProductImages(productNo);

	    // product 테이블은 soft delete
	    adminProductMapper.softDeleteProduct(productNo);
	}
	
	/*
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
*/
	private void insertProductImage(Long productNo, String imageUrl, String imageType, int sortOrder) {
	    if (imageUrl == null || imageUrl.trim().isEmpty()) {
	        return;
	    }

	    adminProductMapper.insertProductImage(productNo, imageUrl, imageType, sortOrder);
	}

	

	

	

	

	
}
