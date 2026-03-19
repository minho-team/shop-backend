package com.shop.controller.user;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.dto.user.cart.CartItemAddRequest;
import com.shop.dto.user.cart.CartItemResponseDTO;
import com.shop.service.user.cart.CartItemService;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart/item")
@RequiredArgsConstructor
public class CartItemController {

	private final CartItemService cartItemService;
	private final MemberService memberService;

	// 장바구니에 상품 추가
	@PostMapping()
	public ResponseEntity<?> addCartItem(Authentication authentication, @RequestBody CartItemAddRequest request) {
		try {
			String userId = authentication.getName();
			Member member = memberService.readOneMember(userId);

			cartItemService.addCartItem(member.getMemberNo(), request);
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
			cartItemService.deleteCartItem(cartItemNo);
			return ResponseEntity.ok("장바구니에서 상품이 삭제되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	// 장바구니 상품 전부 삭제
	@DeleteMapping()
	public ResponseEntity<?> deleteAllCartItem(Authentication authentication) {
		try {
			String userId = authentication.getName();
			Member member = memberService.readOneMember(userId);

			cartItemService.deleteAllCartItem(member.getMemberNo());
			return ResponseEntity.ok("장바구니에서 상품이 모두 삭제되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	// 각 물건의 수량변경
	@PutMapping("/{cartItemNo}")
	public ResponseEntity<?> updateCartItem(Authentication authentication, @PathVariable Long cartItemNo,  @RequestParam int cartQty) {
		try {
			 String userId = authentication.getName();
	            Member member = memberService.readOneMember(userId);
	            
			cartItemService.updateCartItem(member.getMemberNo(), cartItemNo, cartQty);
			return ResponseEntity.ok("수량이 변경되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	// 장바구니에 있는 물건들 읽어오기
	@GetMapping()
	public ResponseEntity<?> readMyCartItems(Authentication authentication) {
		try {
			String userId = authentication.getName();
			Member member = memberService.readOneMember(userId);

			List<CartItemResponseDTO> list = cartItemService.readCartItemByMemberNo(member.getMemberNo());
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
}
