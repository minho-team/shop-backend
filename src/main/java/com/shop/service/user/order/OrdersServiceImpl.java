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
import com.shop.mapper.user.OrderItemMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersServiceImpl implements OrdersService {

	private final OrdersMapper mapper;
	private final OrderItemMapper orderItemMapper;
	private final MemberService memberService;

	@Override
	@Transactional(rollbackFor = Exception.class)
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
		log.info("(OrdersServiceImpl) 주문 완료 -> 회원 번호 {}의 구매 횟수 증가 로직 실행", memberNo);
	    memberService.updateMemberGrade(memberNo);
	    
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

		return OrderDetailResponseDTO.builder().order(order).items(items).build();
	}

	@Override
	@Transactional(rollbackFor = Exception.class) // [1] 트랜잭션 추가 (에러 발생 시 모두 취소)
	public void createOrder(Orders orders) throws Exception { // [2] throws Exception 추가
	    
	    // 1. 먼저 주문을 DB에 저장합니다.
	    mapper.createOrder(orders);
	    
	    // 2. 저장된 orders 객체에서 memberNo를 꺼내옵니다.
	    Long memberNo = orders.getMemberNo(); 

	    if (memberNo != null) {
	        // 3. 구매 횟수 증가 및 등급 업데이트를 호출합니다.
	        memberService.increasePurchaseCount(memberNo);
	        memberService.updateMemberGrade(memberNo);
	        
	        log.info("(OrdersServiceImpl) 주문 저장 완료 -> 회원 {}번 데이터 갱신", memberNo);
	    } else {
	        log.error("주문 정보에 memberNo가 없어 카운트를 올리지 못했습니다.");
	    }
	}

	@Override
	public Orders getOneOrder(Long orderNo) {
		return mapper.getOneOrder(orderNo);
	}

	@Override
	public List<Orders> getAllOrders(Long memberNo) {
		return mapper.getAllOrders(memberNo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void cancelOrder(Long orderNo) throws Exception {
	    // 1. 주문 정보 조회
	    Orders order = mapper.getOneOrder(orderNo); 
	    if (order == null) throw new Exception("주문 정보가 없습니다.");

	    // 2. 상태 검증
	    if (!"PENDING_PAYMENT".equals(order.getOrderStatus())) {
	        throw new Exception("결제대기 상태의 주문만 취소가 가능합니다.");
	    }

	    // 3. 주문 상태 변경 
	    mapper.updateOrderStatus(orderNo, "CANCELED");

	    // 4. 주문 아이템 상태 변경
	    orderItemMapper.updateOrderItemStatusByOrderNo(orderNo, "CANCELED");

	    // 5. 재고 복구 로직
	    List<OrderItemDTO> items = orderItemMapper.selectOrderItemsByOrderNo(orderNo);
	    for (OrderItemDTO item : items) {
	        mapper.increaseProductStock(item.getProductOptionNo(), item.getQuantity());
	    }
	}
	
}