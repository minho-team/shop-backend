package com.shop.controller;

import java.util.List;
import java.util.Map;

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

import com.shop.domain.Member;
import com.shop.domain.Orders;
import com.shop.service.MemberService;
import com.shop.service.OrdersService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {
	
	
	private final OrdersService ordersService;
	private final MemberService memberService;
	
	@PostMapping
	public ResponseEntity<?> createOrder(@RequestBody Orders orders) {
		try {
			ordersService.createOrder(orders);
			return ResponseEntity.ok("주문 생성 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
	// 로그인된 사용자의 모든 주문 내역을 불러온다
	@GetMapping
	public ResponseEntity<?> getAllOrders(Authentication authentication) {
		
		//멤버id는 유니크키이므로 한 튜플을 식별 가능
		String memberId = authentication.getName();
		
		try {
			Member member = memberService.readOneMember(memberId);
			List<Orders> orderList = ordersService.getAllOrders(member.getMemberNo());
			log.info("컨트롤러에서 멤버 id:"+member.getMemberNo());
			log.info("컨트롤러에서 오더리스트:"+orderList);
			return ResponseEntity.ok(Map.of("orderList",orderList,"memberName",member.getName()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
	@GetMapping("/{orderNo}")
	public ResponseEntity<?> getOneOrder(@PathVariable Long orderNo) {
		
		try {
			return ResponseEntity.ok(ordersService.getOneOrder(orderNo));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
	@PutMapping("/{orderNo}")
	public ResponseEntity<?> updateOrder(@PathVariable Long orderNo, @RequestBody Orders orders) {
		orders.setOrderNo(orderNo);
		try {
			ordersService.updateOrder(orders);
			return ResponseEntity.ok("주문 수정 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
	@DeleteMapping("/{orderNo}")
	public ResponseEntity<?> deleteOrder(@PathVariable Long orderNo){
		try {
			ordersService.deleteOrder(orderNo);
			return ResponseEntity.ok("주문 삭제 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
}
















