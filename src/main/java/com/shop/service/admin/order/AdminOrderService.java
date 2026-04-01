package com.shop.service.admin.order;

import com.shop.dto.admin.order.AdminOrderListRequest;
import com.shop.dto.admin.order.AdminOrderListResponse;
import com.shop.dto.admin.order.AdminOrderReadDTO;
import com.shop.dto.admin.order.OrderStatusUpdateRequestDTO;
import com.shop.dto.admin.order.RefundStatusUpdateRequestDTO;

public interface AdminOrderService {
	
	// 관리자 주문 리스트 모두 조회
    AdminOrderListResponse getOrderList(AdminOrderListRequest request) throws Exception;

    AdminOrderReadDTO getOrder(Long orderNo) throws Exception;

    void updateOrderStatus(Long orderNo, OrderStatusUpdateRequestDTO requestDTO) throws Exception;

    void updateRefundStatus(Long orderNo, RefundStatusUpdateRequestDTO requestDTO) throws Exception;
    
    void updateOrderItemStatus(Long orderItemNo, String status) throws Exception;
}
