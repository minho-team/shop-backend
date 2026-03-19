package com.shop.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Cart;
import com.shop.domain.Member;
import com.shop.service.user.cart.CartService;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	private final MemberService memberService;

	// 카트 생성만
	@PostMapping
	public ResponseEntity<?> createCart(Authentication authentication) {
		try {
			String userId = authentication.getName();
			Member member = memberService.readOneMember(userId);

			cartService.createCart(member.getMemberNo());
			return ResponseEntity.ok("장바구니 생성 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	@GetMapping
	public ResponseEntity<?> readAllCart() {
		try {
			return ResponseEntity.ok(cartService.readAllCart());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	@GetMapping("/{cartNo}")
	public ResponseEntity<?> readCart(@PathVariable Long cartNo) {
		try {
			return ResponseEntity.ok(cartService.readCart(cartNo));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	@PutMapping("/{cartNo}")
	public ResponseEntity<?> updateCart(@PathVariable Long cartNo, @RequestBody Cart cart) {
		cart.setCartNo(cartNo);
		try {
			cartService.updateCart(cart);
			return ResponseEntity.ok("장바구니 수정 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	@DeleteMapping("/{cartNo}")
	public ResponseEntity<?> deleteCart(@PathVariable Long cartNo) {
		try {
			cartService.deleteCart(cartNo);
			return ResponseEntity.ok("장바구니 삭제 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
}