package com.shop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shop.domain.CartItem;
import com.shop.dto.CartItemAddRequest;
import com.shop.service.CartItemService;

@Controller
@RequestMapping("/api/cart/item")
public class CartItemController {
	@Autowired
	public CartItemService service;
	
	// 장바구니에 상품 추가
	@PostMapping()
	public ResponseEntity<?> addCartItem(@RequestBody CartItemAddRequest request) {
		try {
			service.addCartItem(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 장바구니 상품 삭제
	@DeleteMapping("/{cartItemNo}")
	public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemNo) {
		try {
			service.deleteCartItem(cartItemNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 장바구니 상품 전부 삭제
	@DeleteMapping()
	public ResponseEntity<?> deleteAllCartItem() {
		try {
			service.deleteAllCartItem();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 각 물건의 수량변경
	@PutMapping("/{cartItemNo}")
	public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemNo) {
		try {
			service.updateCartItem(cartItemNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 장바구니에 있는 물건들 읽어오기
	@GetMapping()
	public ResponseEntity<?> readAllCartItem() {
		try {
			List<CartItem> list = service.readAllCartItem();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
