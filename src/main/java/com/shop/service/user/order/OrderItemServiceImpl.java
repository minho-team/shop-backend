package com.shop.service.user.order;

import java.util.List;
import org.springframework.stereotype.Service;
import com.shop.domain.OrderItem;
import com.shop.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// 이 부분에 인터페이스 이름을 반드시 명시해야 합니다!
public class OrderItemServiceImpl implements OrderItemService {

	private final OrderItemMapper orderItemMapper;

	@Override // 이제 부모 인터페이스의 메서드를 정상적으로 재정의하게 됩니다.
	public List<OrderItem> readByOrderNo(Long orderNo) {
		return orderItemMapper.selectByOrderNo(orderNo);
	}
}