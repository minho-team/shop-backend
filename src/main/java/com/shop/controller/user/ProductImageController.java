package com.shop.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.user.product.ProductImageListDTO;
import com.shop.service.user.product.ProductImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductImageController {

	private final ProductImageService productImageService;

	@GetMapping("/{productNo}/images")
	public ResponseEntity<?> getProductMainAndThumbImages(@PathVariable Long productNo) {
		try {
			ProductImageListDTO result = productImageService.getProductMainAndThumbImages(productNo);

			if (result.getImages() == null || result.getImages().isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 상품의 MAIN/THUMB 이미지가 없습니다.");
			}

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 이미지를 불러오지 못했습니다.");
		}
	}
}
