/*
package com.shop.controller.admin;

import java.nio.file.FileStore;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Product;
import com.shop.dto.admin.product.AdminProductInsertDTO;
import com.shop.dto.user.product.ProductCreateRequest;
import com.shop.dto.user.product.ProductListResponse;
import com.shop.dto.user.product.ProductUpdateRequest;
import com.shop.service.admin.product.AdminProductImageService;
import com.shop.service.admin.product.AdminProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product")
public class AdminProductController {

	private final AdminProductService productService;
	private final AdminProductImageService productImageService;
	private final FileStore fileStore;
	
	
	@GetMapping("/{productNo}")
	public ResponseEntity<?> getOneProduct(@PathVariable Long productNo) {
		try {
			Product product = productService.getOneProduct(productNo);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("하나의 상품을 받아오지 못했습니다.");
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllProductToMainPage() {

		try {
			List<ProductListResponse> list = productService.getAllProductToMainPage();
			
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
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
	
}


*/
