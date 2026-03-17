package com.shop.controller.user;

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
import com.shop.domain.OrderItem;
import com.shop.domain.Orders;
import com.shop.service.user.member.MemberService;
import com.shop.service.user.order.OrderItemService;
import com.shop.service.user.order.OrdersService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

	private final OrdersService ordersService;
	private final MemberService memberService;
	private final OrderItemService orderItemService;

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

		// 멤버id는 유니크키이므로 한 튜플을 식별 가능
		String memberId = authentication.getName();

		try {
			Member member = memberService.readOneMember(memberId);
			List<Orders> orderList = ordersService.getAllOrders(member.getMemberNo());
			log.info("컨트롤러에서 멤버 id:" + member.getMemberNo());
			log.info("컨트롤러에서 오더리스트:" + orderList);
			return ResponseEntity.ok(Map.of("orderList", orderList, "memberName", member.getName()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

	@GetMapping("/{orderNo}")
	public ResponseEntity<?> getOneOrder(@PathVariable Long orderNo, Authentication authentication) {
		try {
			// 1. 주문 상세 정보 조회 (기본 주문 정보)
			Orders order = ordersService.getOneOrder(orderNo);

			if (order == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("주문 정보를 찾을 수 없습니다.");
			}

			// 2. 로그인한 사용자의 회원 정보 조회
			String memberId = authentication.getName();
			Member member = memberService.readOneMember(memberId);

			// [보안 검증] 주문 데이터의 회원번호와 현재 로그인한 회원의 번호가 일치하는지 확인
			if (!order.getMemberNo().equals(member.getMemberNo())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인의 주문 내역만 조회할 수 있습니다.");
			}

			// 3. 주문 상품(OrderItem) 리스트 조회
			// 서비스에 해당 메서드(readByOrderNo 등)가 구현되어 있어야 합니다.
			List<OrderItem> items = orderItemService.readByOrderNo(orderNo);

			// 4. Map을 사용하여 데이터 구조 재구성
			Map<String, Object> resultMap = new java.util.HashMap<>();
			resultMap.put("order", order); // 주문 마스터 정보
			resultMap.put("items", items); // 주문 상품 상세 리스트 (추가)

			// 가입 시 입력한 주문자 주소 정보 (Member 도메인 기준)
			resultMap.put("ordererZipCode", member.getZipCode());
			resultMap.put("ordererBasicAddress", member.getBasicAddress());
			resultMap.put("ordererDetailAddress", member.getDetailAddress());

			return ResponseEntity.ok(resultMap);
		} catch (Exception e) {
			log.error("주문 상세 조회 중 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
		}
	}

}
