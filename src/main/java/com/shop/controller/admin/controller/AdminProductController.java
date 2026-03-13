/*
package com.shop.controller.admin.controller;

import java.nio.file.FileStore;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shop.domain.Product;
import com.shop.dto.ProductCreateRequest;
import com.shop.dto.ProductUpdateRequest;
import com.shop.dto.admin.product.AdminProductCreateDTO;
import com.shop.dto.admin.product.ProductImageDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

	private final AdminProductService apService;
	private final AdminProductImageService apiService;
	private final FileStore fileStore;
	
	@PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> productRegister(
			@RequestParam String name,
			@RequestParam Long categoryId,
			@RequestParam int price,
			@RequestParam(required=false) String description,
			@RequestParam(required=true) MultipartFile thumbImage,
			@RequestParam(required=false) List<MultipartFile> galleryImages,
			AdminProductCreateDTO createDTO, ProductImageDTO imageDTO)throws Exception{
		if (thumbImage != null && !thumbImage.isEmpty()) {
            
                return ResponseEntity.badRequest().body("images/types 개수 불일치");
            }
		
		return null;
	}
	
	/*
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> insertProduct(@RequestBody ProductCreateRequest dto) {
		try {
			productService.insertProduct(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/{productNo}")
	public ResponseEntity<?> getOneProduct(@PathVariable Long productNo) {
		try {
			productService.getOneProduct(productNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping
	public ResponseEntity<?> getAllProduct() {

		List<Product> list = null;
		try {
			list = productService.getAllProducts();
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
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