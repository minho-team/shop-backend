package com.shop.service.user.order;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shop.domain.OrderItem;
import com.shop.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class OrderItemServiceImpl implements OrderItemService {

	private final OrderItemMapper orderItemMapper;

	@Override 
	public List<OrderItem> readByOrderNo(Long orderNo) {
		return orderItemMapper.selectByOrderNo(orderNo);
	}
}