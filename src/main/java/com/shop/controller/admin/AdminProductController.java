package com.shop.controller.admin;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.admin.product.AdminProductInsertDTO;
import com.shop.dto.admin.product.AdminProductPageResponseDTO;
import com.shop.dto.admin.product.AdminProductReadDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;
import com.shop.service.admin.product.AdminProductService;
import com.shop.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product")
public class AdminProductController {

	private final AdminProductService productService;
	//private final AdminProductImageService productImageService;
	private final CustomFileUtil customFileUtil;
	
	
	// 관리자 상품 목록 조회
	@GetMapping
	public ResponseEntity<?> getProductList(AdminProductSearchDTO searchDTO) {
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
	
	
	@PostMapping(consumes = "multipart/form-data")
	public ResponseEntity<?> insertProduct(@ModelAttribute AdminProductInsertDTO dto) {
		
		log.info("thumbImage = " + dto.getThumbImage());
		log.info("mainImage = " + dto.getMainImage());
		
		try {
			// 썸네일 필수 검사
	        if (dto.getThumbImage() == null || dto.getThumbImage().isEmpty()) {
	            return ResponseEntity.badRequest().body("썸네일 이미지는 필수입니다.");
	        }
			productService.insertProduct(dto);
			return ResponseEntity.ok("상품 등록 완료");
		// 사용자가 잘못 입력한 경우	
		} catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    // 그 외 예외
	    } catch (Exception e) {
	    		log.error("상품 등록 실패", e);
	        return ResponseEntity.badRequest().body("상품 등록 실패");
	    }
	}
	
	/*
	@PutMapping("/{productNo}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateProduct(@PathVariable Long productNo, @RequestBody ProductUpdateRequest dto) {
		try {
			productService.updateProducts(productNo, dto);
			return ResponseEntity.ok("업데이트 완료");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
	}

	@DeleteMapping("/{productNo}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteProduct(@PathVariable Long productNo) {
		try {
			productService.deleteProduct(productNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
}


