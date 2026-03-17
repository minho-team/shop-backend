package com.shop.service.user.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderResponseDTO;
import com.shop.mapper.OrdersMapper;

@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersMapper mapper;

	@Override
	public void createOrder(Orders orders) {
		mapper.createOrder(orders);
	}

	// [신규] 마이페이지 전용 페이징 메서드
	@Override
	public OrderResponseDTO getMyOrderList(Long memberNo, int page) {
	    int size = 10; 
	    int startRow = (page - 1) * size + 1;
	    int endRow = page * size;

	   
	    List<com.shop.dto.user.order.OrderDTO> list = mapper.getMyOrderList(memberNo, startRow, endRow);

	    int totalCount = mapper.getTotalCount(memberNo);

	    // DTO 생성자도 List<OrderDTO>를 받도록 되어있는지 확인이 필요합니다.
	    return new OrderResponseDTO(list, totalCount, page, size);
	}

	@Override
	public Orders getOneOrder(Long orderNo) {
		return mapper.getOneOrder(orderNo);
	}

	// [유지] 기존 전체 조회 메서드 복구 (기존 기능 방해 금지)
	@Override
	public List<Orders> getAllOrders(Long memberNo) throws Exception {
		return mapper.getAllOrders(memberNo);
	}
}