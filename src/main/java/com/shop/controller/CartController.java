package com.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shop.dto.CartItemAddRequest;
import com.shop.service.CartService;
import com.shop.service.MemberService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    private MemberService memberService;

    @PostMapping
    public ResponseEntity<?> createCart(Authentication authentication, @RequestBody CartItemAddRequest dto) {
        try {
        	String userId = authentication.getName();
        	Member member = memberService.readOneMember(userId);
        	cartService.createCart(member.getMemberNo(), dto);
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