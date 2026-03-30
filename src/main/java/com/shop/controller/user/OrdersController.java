package com.shop.controller.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.Member;
import com.shop.dto.user.order.OrderCreateRequestDTO;
import com.shop.dto.user.order.OrderCreateResponseDTO;
import com.shop.dto.user.order.OrderDetailResponseDTO;
import com.shop.dto.user.order.OrderItemDTO;
import com.shop.dto.user.order.OrderResponseDTO;
import com.shop.service.user.member.MemberService;
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

	@PostMapping
	public ResponseEntity<?> createOrder(@RequestBody OrderCreateRequestDTO request, Authentication authentication) {
		try {
			if (authentication == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			String memberId = authentication.getName();
			Member member = memberService.readOneMember(memberId);

			if (member == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다.");
			}

			OrderCreateResponseDTO response = ordersService.createOrder(request, member.getMemberNo());
			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			log.error("주문 생성 유효성 오류", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		} catch (Exception e) {
			log.error("주문 생성 중 오류", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주문 생성 실패: " + e.getMessage());
		}
	}

	// 로그인된 사용자의 모든 주문 내역을 불러온다
	@GetMapping
	public ResponseEntity<?> getMyOrderList(Authentication authentication,
			@RequestParam(value = "page", defaultValue = "1") int page) { // page 파라미터 수신 확인

		log.info("orders컨트롤러의 getMyOrderList");
		String memberId = authentication.getName();
		try {
			Member member = memberService.readOneMember(memberId);
			log.info("orders컨트롤러의 getMyOrderList결과1:" + page);

			// 중요: 반드시 getMyOrderList를 호출해서 OrderResponseDTO를 받아야 함!
			OrderResponseDTO response = ordersService.getMyOrderList(member.getMemberNo(), page);
			log.info("orders컨트롤러의 getMyOrderList결과2:" + response);
			// DTO 자체를 리턴 (이 안에 리스트와 페이지 정보가 다 들어있음)

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("에러발생");
		}
	}

	@GetMapping("/{orderNo}")
	public ResponseEntity<?> getOneOrder(@PathVariable Long orderNo, Authentication authentication) {
		try {
			log.info("getOneOrder 진입");
			// 1. [수정] 서비스의 getOrderDetail을 호출하여 주문+상품 정보를 한 번에 가져옵니다.
			// 이 안에 이미 order와 items가 다 들어있습니다.
			OrderDetailResponseDTO detail = ordersService.getOrderDetail(orderNo);
			log.info("OrderDetailResponseDTO detail:"+detail);
			if (detail == null || detail.getOrder() == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("주문 정보를 찾을 수 없습니다.");
			}

			// 2. 로그인 정보 및 권한 체크
			String memberId = authentication.getName();
			Member member = memberService.readOneMember(memberId);

			// 상세 정보 내의 order 객체에서 회원 번호를 확인합니다.
			if (!detail.getOrder().getMemberNo().equals(member.getMemberNo())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인의 주문 내역만 조회할 수 있습니다.");
			}

			// 3. 리액트가 기대하는 구조(Map)로 데이터 구성
			Map<String, Object> resultMap = new java.util.HashMap<>();
			resultMap.put("order", detail.getOrder());
			resultMap.put("items", detail.getItems());
			
			for(OrderItemDTO item : detail.getItems()) {
				log.info("item내려가는지"+item.getOrderItemStatus());
			}

			// 회원 주소 정보 추가
			resultMap.put("ordererZipCode", member.getZipCode());
			resultMap.put("ordererBasicAddress", member.getBasicAddress());
			resultMap.put("ordererDetailAddress", member.getDetailAddress());

			return ResponseEntity.ok(resultMap);
		} catch (Exception e) {
			log.error("주문 상세 조회 에러: ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("에러 발생: " + e.getMessage());
		}
	}
	
	@PutMapping("/{orderNo}/cancel")
	public ResponseEntity<?> cancelOrder(@PathVariable Long orderNo, Authentication authentication) {
	    try {
	        ordersService.cancelOrder(orderNo);
	        return ResponseEntity.ok("주문이 정상적으로 취소되었습니다.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}

}
