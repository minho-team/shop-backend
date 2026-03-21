package com.shop.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.admin.order.AdminOrderItemDetailResponseDTO;
import com.shop.dto.admin.order.AdminOrderItemStatusUpdateRequestDTO;
import com.shop.service.admin.order.AdminOrderItemService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/admin/orders/item")
@RequiredArgsConstructor
public class AdminOrdersItemController {
	//관리자가 주문 내역 안의 주문 아이템들에 대해 관리할 때 쓰일 컨트롤러
	private final AdminOrderItemService adminOrderItemService;

	// 관리자 order_item 목록 조회
	@GetMapping("/{orderNo}")
	public ResponseEntity<?> getOrderItemList(@PathVariable Long orderNo) throws Exception {
		try {
			List<AdminOrderItemDetailResponseDTO> list = adminOrderItemService.getOrderItemList(orderNo);
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("order_item 목록 조회중 오류가 발생하였습니다.");
		}
	}
	
	
	@PatchMapping("/{orderItemNo}/status")
    public ResponseEntity<String> updateOrderItemStatus(
            @PathVariable Long orderItemNo,
            @RequestBody AdminOrderItemStatusUpdateRequestDTO requestDTO
    ) {
        System.out.println("orderItemNo = " + orderItemNo);
        System.out.println("orderItemStatus = " + requestDTO.getOrderItemStatus());

        try {
			adminOrderItemService.updateOrderItemStatus(orderItemNo, requestDTO.getOrderItemStatus());
			return ResponseEntity.ok("주문상품 상태 변경 완료");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("주문상품 상태 변경 실패");

		}
    }
}







