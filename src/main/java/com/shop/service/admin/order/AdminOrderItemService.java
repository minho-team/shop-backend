package com.shop.service.admin.order;

import java.util.List;

import com.shop.domain.OrderItem;
import com.shop.dto.admin.order.AdminOrderItemDetailResponseDTO;

public interface AdminOrderItemService {

	List<AdminOrderItemDetailResponseDTO> getOrderItemList(Long orderNo) throws Exception;

	void updateOrderItemStatus(Long orderItemNo, String orderItemStatus) throws Exception;
	
}
