package com.shop.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.service.user.order.OrdersService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders/item")
@RequiredArgsConstructor
@Slf4j
public class OrdersItemController {

    private final OrdersService ordersService;

    @PutMapping("/{orderItemNo}/refund-complete")
    public ResponseEntity<?> completeItemRefund(@PathVariable Long orderItemNo, Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 환불 완료 및 리뷰 자동 삭제 트랜잭션 호출
            ordersService.completeRefund(orderItemNo);
            
            return ResponseEntity.ok("해당 상품의 환불 처리가 완료되었으며, 관련 리뷰가 삭제되었습니다.");
            
        } catch (Exception e) {
            log.error("단일 상품 환불 처리 중 예외 발생 (orderItemNo: {})", orderItemNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("환불 처리 실패: " + e.getMessage());
        }
    }
}