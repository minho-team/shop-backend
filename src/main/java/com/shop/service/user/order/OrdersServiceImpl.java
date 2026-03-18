package com.shop.service.user.order;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.OrderItem;
import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderCreateRequestDTO;
import com.shop.dto.user.order.OrderCreateResponseDTO;
import com.shop.dto.user.order.OrderItemCreateRequestDTO;
import com.shop.dto.user.order.OrderResponseDTO;
import com.shop.mapper.OrderItemMapper;
import com.shop.mapper.OrdersMapper;

@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersMapper mapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;

	@Override
	public void createOrder(Orders orders) {
		mapper.createOrder(orders);
	}

	@Override
	@Transactional
	public OrderCreateResponseDTO createOrder(OrderCreateRequestDTO request, Long memberNo) throws Exception {
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("주문 상품이 없습니다.");
		}

		Orders orders = new Orders();
		orders.setMemberNo(memberNo);
		orders.setOrdererName(request.getOrdererName());
		orders.setOrdererPhoneNumber(request.getOrdererPhoneNumber());
		orders.setOrdererEmail(request.getOrdererEmail());
		orders.setTotalPrice(request.getTotalPrice());
		orders.setReceiverName(request.getReceiverName());
		orders.setReceiverPhoneNumber(request.getReceiverPhoneNumber());
		orders.setReceiverZipCode(request.getReceiverZipCode());
		orders.setReceiverBaseAddress(request.getReceiverBaseAddress());
		orders.setReceiverDetailAddress(request.getReceiverDetailAddress());
		orders.setMessage(request.getMessage() == null || request.getMessage().trim().isEmpty() ? null
				: request.getMessage().trim());

		mapper.createOrder(orders);

		for (OrderItemCreateRequestDTO itemRequest : request.getItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(orders.getOrderNo());
			orderItem.setProductOptionNo(itemRequest.getProductOptionNo());
			orderItem.setQuantity(itemRequest.getQuantity());
			orderItem.setUnitPrice(itemRequest.getUnitPrice());
			orderItem.setItemName(itemRequest.getItemName());
			orderItem.setItemSize(itemRequest.getItemSize());
			orderItem.setItemColor(itemRequest.getItemColor());

			orderItemMapper.insertOrderItem(orderItem);
		}

		return new OrderCreateResponseDTO(orders.getOrderNo(), new Date(), orders.getTotalPrice());
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