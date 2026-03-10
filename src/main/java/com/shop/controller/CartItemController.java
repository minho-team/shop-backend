package com.shop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.CartItem;
import com.shop.dto.CartItemAddRequest;
import com.shop.service.CartItemService;

@RestController
@RequestMapping("/api/cart/item")
public class CartItemController {
	@Autowired
	public CartItemService service;
	
	// 장바구니에 상품 추가
	@PostMapping()
	public ResponseEntity<?> addCartItem(@RequestBody CartItemAddRequest request) {
		try {
			//service.addCartItem(request);
			return ResponseEntity.ok("장바구니에 상품이 추가되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	// 장바구니 상품 삭제
	@DeleteMapping("/{cartItemNo}")
	public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemNo) {
		try {
			service.deleteCartItem(cartItemNo);
			return ResponseEntity.ok("장바구니에서 상품이 삭제되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	// 장바구니 상품 전부 삭제
	@DeleteMapping()
	public ResponseEntity<?> deleteAllCartItem() {
		try {
			service.deleteAllCartItem();
			return ResponseEntity.ok("장바구니에서 상품이 모두 삭제되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	// 각 물건의 수량변경
	@PutMapping("/{cartItemNo}")
	public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemNo) {
		try {
			service.updateCartItem(cartItemNo);
			return ResponseEntity.ok("수량이 변경되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	// 장바구니에 있는 물건들 읽어오기
	@GetMapping()
	public ResponseEntity<?> readAllCartItem() {
		try {
			List<CartItem> list = service.readAllCartItem();
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
}
