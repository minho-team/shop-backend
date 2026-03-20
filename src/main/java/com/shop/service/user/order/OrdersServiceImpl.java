package com.shop.service.user.order;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.OrderItem;
import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderCreateRequestDTO;
import com.shop.dto.user.order.OrderCreateResponseDTO;
import com.shop.dto.user.order.OrderDTO;
import com.shop.dto.user.order.OrderDetailResponseDTO;
import com.shop.dto.user.order.OrderItemCreateRequestDTO;
import com.shop.dto.user.order.OrderItemDTO;
import com.shop.dto.user.order.OrderResponseDTO;
import com.shop.mapper.OrderItemMapper;
import com.shop.mapper.OrdersMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersServiceImpl implements OrdersService {

	private final OrdersMapper mapper;
	private final OrderItemMapper orderItemMapper;

	// 주문서 작성 페이지에서 결제하기 버튼을 누르면 작동되는 함수
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
		orders.setMessage(request.getMessage());

		mapper.createOrder(orders);

		for (OrderItemCreateRequestDTO itemRequest : request.getItems()) {
			OrderItem orderItem = new OrderItem();

			if (orders.getOrderNo() != null) {
				log.info("(OrdersServiceImpl) getOrderNo가 null이 아니면");
				orderItem.setOrderNo(orders.getOrderNo());
			}

			if (itemRequest.getProductOptionNo() != null) {
				orderItem.setProductOptionNo(itemRequest.getProductOptionNo());
			}

			orderItem.setQuantity(itemRequest.getQuantity());
			orderItem.setUnitPrice(itemRequest.getUnitPrice());
			orderItem.setItemName(itemRequest.getItemName());
			orderItem.setItemSize(itemRequest.getItemSize());
			orderItem.setItemColor(itemRequest.getItemColor());

			orderItemMapper.insertOrderItem(orderItem);
		}

		return new OrderCreateResponseDTO(orders.getOrderNo(), new Date(), orders.getTotalPrice());
	}

	@Override
	@Transactional(readOnly = true)
	public OrderResponseDTO getMyOrderList(Long memberNo, int page) {
		int size = 10;
		int startRow = (page - 1) * size + 1;
		int endRow = page * size;
		List<OrderDTO> list = mapper.getMyOrderList(memberNo, startRow, endRow);
		int totalCount = mapper.getTotalCount(memberNo);
		return new OrderResponseDTO(list, totalCount, page, size);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderDetailResponseDTO getOrderDetail(Long orderNo) throws Exception {
Orders order = mapper.getOneOrder(orderNo);
		
		// 1. DTO 리스트로 데이터를 받아옵니다 (Mapper 반환 타입 확인 필수)
		List<OrderItemDTO> items = mapper.getOrderItemList(orderNo);
		
		// 2. 다른 페이지와 동일하게 이미지 URL에 /upload/ 경로를 붙여줍니다.
		if (items != null) {
			for (OrderItemDTO item : items) {
				if (item.getImageUrl() != null && !item.getImageUrl().isBlank()) {
					item.setImageUrl("/upload/" + item.getImageUrl());
				}
			}
		}
		
		return OrderDetailResponseDTO.builder()
				.order(order)
				.items(items)
				.build();
	}

	@Override
	public void createOrder(Orders orders) {
		mapper.createOrder(orders);
	}

	@Override
	public Orders getOneOrder(Long orderNo) {
		return mapper.getOneOrder(orderNo);
	}

	@Override
	public List<Orders> getAllOrders(Long memberNo) {
		return mapper.getAllOrders(memberNo);
	}
}