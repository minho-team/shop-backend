package com.shop.controller.admin;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.admin.product.AdminProductPageResponseDTO;
import com.shop.dto.admin.product.AdminProductReadDTO;
import com.shop.dto.admin.product.AdminProductSearchDTO;
import com.shop.service.admin.product.AdminProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product")
public class AdminProductController {

	private final AdminProductService productService;
	//private final AdminProductImageService productImageService;
	//private final FileStore fileStore;
	
	
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
	
	/*
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> insertProduct(@RequestBody AdminProductInsertDTO dto) {
		try {
			productService.insertProduct(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
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


