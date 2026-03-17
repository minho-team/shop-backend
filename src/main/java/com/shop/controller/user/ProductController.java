package com.shop.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.user.product.ProductDetailResponse;
import com.shop.dto.user.product.ProductListResponse;
import com.shop.dto.user.product.ProductListResponseDto;
import com.shop.service.user.product.ProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/{productNo}")
	public ResponseEntity<?> getOneProduct(@PathVariable Long productNo) {
		try {
			ProductDetailResponse response = productService.getOneProduct(productNo);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 정보를 받아오지 못했습니다.");
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

	@GetMapping("/withcategory")
    public ResponseEntity<?> getProductListWithCategory(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword
    ) {
		try {
			List<ProductListResponseDto> list = productService.selectSearchProductList(categoryId, keyword);
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
    }

}
