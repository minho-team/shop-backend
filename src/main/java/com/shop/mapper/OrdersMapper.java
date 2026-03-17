package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.shop.domain.Orders;
import com.shop.dto.admin.order.AdminOrderDto;
import com.shop.dto.admin.order.AdminOrderListRequest;

@Mapper
public interface OrdersMapper {

	void createOrder(Orders orders);

	List<Orders> getAllOrders(Long memberNo);

	Orders getOneOrder(Long orderNo);


	
}
