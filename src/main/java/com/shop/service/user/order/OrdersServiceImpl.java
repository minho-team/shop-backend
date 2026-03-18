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
import com.shop.dto.user.order.OrderDetailResponseDTO;
import com.shop.dto.user.order.OrderResponseDTO;
import com.shop.mapper.OrderItemMapper;
import com.shop.mapper.OrdersMapper;

@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersMapper mapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;

    // 1. 주문 생성
    @Override
    @Transactional
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


	@Override
	public Orders getOneOrder(Long orderNo) {
		return mapper.getOneOrder(orderNo);
	}

    // 2. 마이페이지 주문 목록 조회 (페이징 처리)
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getMyOrderList(Long memberNo, int page) {
        int size = 10;
        int startRow = (page - 1) * size + 1;
        int endRow = page * size;
        
        // 10개씩 끊어서 가져오기
        List<com.shop.dto.user.order.OrderDTO> list = mapper.getMyOrderList(memberNo, startRow, endRow);
        // 전체 주문 개수 가져오기
        int totalCount = mapper.getTotalCount(memberNo);
        
        return new OrderResponseDTO(list, totalCount, page, size);
    }

    // 3. [상세조회 핵심] 주문 정보와 상품 상세 리스트를 DTO에 담아서 반환
    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO getOrderDetail(Long orderNo) throws Exception {
        // 주문 마스터 정보 (Orders)
        Orders order = mapper.getOneOrder(orderNo);
        // 주문 상세 상품들 (List<OrderItem>)
        List<OrderItem> items = mapper.getOrderItemList(orderNo); 
        
        return OrderDetailResponseDTO.builder()
                .order(order)
                .items(items)
                .build();
    }


    // 5. [수정 완료] 전체 주문 내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<Orders> getAllOrders(Long memberNo) throws Exception {
        // null 대신 매퍼 호출로 변경!
        return mapper.getAllOrders(memberNo);
    }
}