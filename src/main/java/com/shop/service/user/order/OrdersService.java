package com.shop.service.user.order;

import java.util.List;

import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderCreateRequestDTO;
import com.shop.dto.user.order.OrderCreateResponseDTO;
import com.shop.dto.user.order.OrderResponseDTO;

public interface OrdersService {

	void createOrder(Orders orders) throws Exception;

	OrderCreateResponseDTO createOrder(OrderCreateRequestDTO request, Long memberNo) throws Exception;
	 
	List<Orders> getAllOrders(Long memberNo) throws Exception;
	
	OrderResponseDTO getMyOrderList(Long memberNo, int page) throws Exception;

	Orders getOneOrder(Long orderNo) throws Exception;

}
