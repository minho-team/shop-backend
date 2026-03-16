package com.shop.service.user.order;

import java.util.List;

import com.shop.domain.OrderItem;

public interface OrderItemService {
	
	List<OrderItem> readByOrderNo(Long orderNo);

}
