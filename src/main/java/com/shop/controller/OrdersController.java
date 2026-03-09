package com.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Orders;
import com.shop.service.OrdersService;




@RestController
@RequestMapping("/api/order")
public class OrdersController {
	
	@Autowired
	private OrdersService service;
	
	@PostMapping
	public ResponseEntity<?> createOrder(@RequestBody Orders orders) {
		try {
			service.createOrder(orders);
			return ResponseEntity.ok("주문 생성 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
	@GetMapping
	public ResponseEntity<?> getAllOrders() {
		
		try {
			return ResponseEntity.ok(service.getAllOrders());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
	@GetMapping("/{orderNo}")
	public ResponseEntity<?> getOneOrder(@PathVariable Long orderNo) {
		
		try {
			return ResponseEntity.ok(service.getOneOrder(orderNo));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
	@PutMapping("/{orderNo}")
	public ResponseEntity<?> updateOrder(@PathVariable Long orderNo, @RequestBody Orders orders) {
		orders.setOrderNo(orderNo);
		try {
			service.updateOrder(orders);
			return ResponseEntity.ok("주문 수정 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
	
	@DeleteMapping("/{orderNo}")
	public ResponseEntity<?> deleteOrder(@PathVariable Long orderNo){
		try {
			service.deleteOrder(orderNo);
			return ResponseEntity.ok("주문 삭제 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}
}
















