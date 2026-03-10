package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.shop.domain.Orders;

@Mapper
public interface OrdersMapper {

	void createOrder(Orders orders);

	List<Orders> getAllOrders();

	Orders getOneOrder(Long orderNo);

	void updateOrder(Orders orders);

	void deleteOrder(Long orderNo);
	
}
