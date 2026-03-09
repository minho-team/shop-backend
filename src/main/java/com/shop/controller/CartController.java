package com.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shop.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/cart")
@RequiredArgsConstructor

public class CartController {

	private final CartService cartService;

	@PostMapping()
	public ResponseEntity<?> createCart() {
		return ResponseEntity.ok(cartService.createCart());
	}

	@PutMapping("/{cartNo}")
	public ResponseEntity<?> updateCart(@PathVariable Long cartNo) {
		return ResponseEntity.ok(cartService.updateCart(cartNo));
	}

	@DeleteMapping("/{cartNo}")
	public ResponseEntity<?> deleteCart(@PathVariable Long cartNo) {
		return ResponseEntity.ok(cartService.deleteCart(cartNo));
	}

	@GetMapping("/{cartNo}")
	public ResponseEntity<?> readCart(@PathVariable Long cartNo) {
		return ResponseEntity.ok(cartService.readCart(cartNo));
	}

	@GetMapping()
	public ResponseEntity<?> readAllCart() {
		return ResponseEntity.ok(cartService.readAllCart());
	}
}
