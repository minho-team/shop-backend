package com.shop.service.admin.order;

import java.util.List;

import com.shop.dto.admin.order.AdminOrderListDTO;
import com.shop.dto.admin.order.AdminOrderReadDTO;
import com.shop.dto.admin.order.AdminOrderSearchDTO;
import com.shop.dto.admin.order.OrderStatusUpdateRequestDTO;
import com.shop.dto.admin.order.RefundStatusUpdateRequestDTO;

public interface AdminOrderService {
	
	List<AdminOrderListDTO> getOrderList(AdminOrderSearchDTO searchDTO) throws Exception;

    AdminOrderReadDTO getOrder(Long orderNo) throws Exception;

    void updateOrderStatus(Long orderNo, OrderStatusUpdateRequestDTO requestDTO) throws Exception;

    void updateRefundStatus(Long orderNo, RefundStatusUpdateRequestDTO requestDTO) throws Exception;
}
