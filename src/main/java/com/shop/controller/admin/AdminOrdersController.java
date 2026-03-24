package com.shop.controller.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.admin.order.AdminOrderListRequest;
import com.shop.dto.admin.order.AdminOrderListResponse;
import com.shop.dto.admin.order.OrderStatusUpdateRequestDTO;
import com.shop.dto.admin.order.RefundStatusUpdateRequestDTO;
import com.shop.service.admin.order.AdminOrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/orders")
@Slf4j
public class AdminOrdersController {

	@Autowired
	private AdminOrderService adminOrderService;

	 // 전체 주문 목록 조회 + 검색/필터
    @GetMapping
    public ResponseEntity<?> getOrderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String datePreset,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String orderStatus
    ) {
        try {
            AdminOrderListRequest request = new AdminOrderListRequest();
            request.setPage(page);
            request.setSize(size);
            request.setSearchType(searchType);
            request.setKeyword(keyword);
            request.setDatePreset(datePreset);
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            request.setOrderStatus(orderStatus);

            AdminOrderListResponse response = adminOrderService.getOrderList(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("주문 목록 조회 실패");
        }
    }

	// 주문 상세 조회
	@GetMapping("/{orderNo}")
	public ResponseEntity<?> getOrder(@PathVariable Long orderNo) {
		try {
			return ResponseEntity.ok(adminOrderService.getOrder(orderNo));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("주문을 찾을 수 없습니다.");
		}
	}

	// 주문 상태 변경
	@PutMapping("/{orderNo}/status")
	public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderNo,
			@RequestBody OrderStatusUpdateRequestDTO requestDTO) {
		try {
			adminOrderService.updateOrderStatus(orderNo, requestDTO);

			return ResponseEntity.ok()
					.body(Map.of("result", "success", "orderNo", orderNo, "orderStatus", requestDTO.getOrderStatus()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("주문 상태 변경 실패");
		}
	}

	// 환불 상태 변경
	@PutMapping("/{orderNo}/refund-status")
	public ResponseEntity<?> updateRefundStatus(@PathVariable Long orderNo,
			@RequestBody RefundStatusUpdateRequestDTO requestDTO) {
		try {
			adminOrderService.updateRefundStatus(orderNo, requestDTO);

			return ResponseEntity.ok().body(
					Map.of("result", "success", "orderNo", orderNo, "refundStatus", requestDTO.getRefundStatus()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("환불 상태 변경 실패");
		}
	}

}
