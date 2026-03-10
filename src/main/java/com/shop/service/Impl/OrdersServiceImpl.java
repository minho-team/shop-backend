package com.shop.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.domain.Orders;
import com.shop.mapper.OrdersMapper;
import com.shop.service.OrdersService;

@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersMapper mapper;
	
	@Override
	public void createOrder(Orders orders) {
		mapper.createOrder(orders);
	}

	@Override
	public List<Orders> getAllOrders() {
		
		return mapper.getAllOrders();
	}

	@Override
	public Orders getOneOrder(Long orderNo) {
		
		return mapper.getOneOrder(orderNo);
	}

	@Override
	public void updateOrder(Orders orders) {
		
		mapper.updateOrder(orders);
	}

	@Override
	public void deleteOrder(Long orderNo) {
		
		mapper.deleteOrder(orderNo);
	}

}
