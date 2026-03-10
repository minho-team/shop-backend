package com.shop.service;

import java.util.List;


import com.shop.domain.Orders;

public interface OrdersService {

	void createOrder(Orders orders) throws Exception;

	List<Orders> getAllOrders() throws Exception;

	Orders getOneOrder(Long orderNo) throws Exception;

	void updateOrder(Orders orders) throws Exception;

	void deleteOrder(Long orderNo) throws Exception;
	
	
}
