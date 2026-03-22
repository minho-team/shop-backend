package com.shop.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.admin.product.AdminProductBasicUpdateDTO;
import com.shop.dto.admin.product.AdminProductInsertDTO;
import com.shop.dto.admin.product.AdminProductOptionRequestDTO;
import com.shop.dto.admin.product.AdminProductPageResponseDTO;
import com.shop.dto.admin.product.AdminProductReadDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;
import com.shop.service.admin.product.AdminProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product")
public class AdminProductController {

	private final AdminProductService productService;

	// 관리자 상품 목록 조회
	@GetMapping
	public ResponseEntity<?> getProductList(AdminProductSearchDTO searchDTO) throws Exception {
		try {
			AdminProductPageResponseDTO list = productService.getProductList(searchDTO);
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 목록 조회중 오류가 발생하였습니다.");
		}
	}

	// 관리자 상품 상세 조회
	@GetMapping("/{productNo}")
	public ResponseEntity<?> getOneProduct(@PathVariable Long productNo) {
		try {
			AdminProductReadDTO productDetail = productService.getProduct(productNo);
			return ResponseEntity.ok(productDetail);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 상세 조회 중 오류가 발생했습니다.");
		}
	}
	
	// 상품 옵션 조회
	@GetMapping("/{productNo}/options")
	public ResponseEntity<?> getProductOptions(@PathVariable Long productNo) {
	    try {
	        return ResponseEntity.ok(productService.getProductOptions(productNo));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("상품 옵션 조회 중 오류가 발생했습니다.");
	    }
	}
	
	// 상품 등록
	@PostMapping(consumes = "multipart/form-data")
	public ResponseEntity<?> insertProduct(@ModelAttribute AdminProductInsertDTO dto) {

		log.info("name={}", dto.getName());
		log.info("categoryId={}", dto.getCategoryId());
		log.info("price={}", dto.getPrice());
		log.info("discountRate={}", dto.getDiscountRate());
		log.info("description={}", dto.getDescription());
		log.info("useYn={}", dto.getUseYn());
		log.info("sameDayDeliveryYn={}", dto.getSameDayDeliveryYn());
		log.info("thumbImage={}", dto.getThumbImage());
		log.info("mainImage={}", dto.getMainImage());
		log.info("galleryImages={}", dto.getGalleryImages());
		log.info("sizeImage={}", dto.getSizeImage());
		log.info("options={}", dto.getOptions());

		try {
			if (dto.getName() == null || dto.getName().isBlank()) {
				return ResponseEntity.badRequest().body("상품명은 필수입니다.");
			}
			if (dto.getCategoryId() == null) {
				return ResponseEntity.badRequest().body("카테고리는 필수입니다.");
			}
			if (dto.getPrice() == null) {
				return ResponseEntity.badRequest().body("가격은 필수입니다.");
			}
			if (dto.getThumbImage() == null || dto.getThumbImage().isEmpty()) {
				return ResponseEntity.badRequest().body("썸네일 이미지는 필수입니다.");
			}

			productService.insertProduct(dto);
			return ResponseEntity.ok("상품 등록 완료");

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			log.error("상품 등록 실패", e);
			return ResponseEntity.badRequest().body("상품 등록 실패");
		}
	}
	
	// 상품 기본정보 수정
	@PutMapping("/{productNo}/basic")
	public ResponseEntity<?> updateProduct(@PathVariable Long productNo, @RequestBody AdminProductBasicUpdateDTO dto) {
		try {
			productService.updateProductsBasic(productNo, dto);
			return ResponseEntity.ok("상품 기본정보 수정 완료");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 기본정보 수정 중 오류가 발생했습니다.");
		}
	}
	
	// 상품 옵션 추가
	@PostMapping("/{productNo}/options")
	public ResponseEntity<?> insertProductOption(
	        @PathVariable Long productNo,
	        @RequestBody AdminProductOptionRequestDTO dto) {
	    try {
	        productService.insertProductOption(productNo, dto);
	        return ResponseEntity.ok("상품 옵션 추가 완료");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 옵션 추가 중 오류가 발생했습니다.");
	    }
	}
	
	// 상품 옵션 수정
	@PutMapping("/{productNo}/options/{productOptionNo}")
	public ResponseEntity<?> updateProductOption(
	        @PathVariable Long productNo,
	        @PathVariable Long productOptionNo,
	        @RequestBody AdminProductOptionRequestDTO dto) {
	    try {
	        productService.updateProductOption(productNo, productOptionNo, dto);
	        return ResponseEntity.ok("상품 옵션 수정 완료");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 옵션 수정 중 오류가 발생했습니다.");
	    }
	}
	
	// 상품 옵션 삭제
	@DeleteMapping("/{productNo}/options/{productOptionNo}")
	public ResponseEntity<?> deleteProductOption(@PathVariable Long productNo,
	        										@PathVariable Long productOptionNo) {
	    try {
	        productService.deleteProductOption(productNo, productOptionNo);
	        return ResponseEntity.ok("상품 옵션 삭제 완료");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 옵션 삭제 중 오류가 발생했습니다.");
	    }
	}
	
	// 상품 삭제
	@DeleteMapping("/{productNo}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteProduct(@PathVariable Long productNo) {
	    try {
	        productService.deleteProduct(productNo);
	        return ResponseEntity.ok("상품 삭제 완료");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 삭제 중 오류가 발생했습니다.");
	    }
	}
	

}
