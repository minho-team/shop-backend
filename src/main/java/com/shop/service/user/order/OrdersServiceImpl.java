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
		log.info("받은 주문 번호: {}", request.getOrderNo());
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("주문 상품이 없습니다.");
		}

		Long existingOrderNo = request.getOrderNo();
		Orders orders;

		if (existingOrderNo != null && existingOrderNo > 0) {
			// [결제 대기시 재결제 로직] 기존 주문정보를 가져옴
			orders = mapper.getOneOrder(existingOrderNo);
			if (orders == null)
				throw new Exception("존재하지 않는 주문 번호입니다.");

			// 기존 아이템 삭제
			orderItemMapper.deleteByOrderNo(existingOrderNo);

			// 변경된 배송지 정보 및 금액 업데이트
			orders.setReceiverName(request.getReceiverName());
	        orders.setReceiverPhoneNumber(request.getReceiverPhoneNumber());
	        orders.setReceiverZipCode(request.getReceiverZipCode());
	        orders.setReceiverBaseAddress(request.getReceiverBaseAddress());
	        orders.setReceiverDetailAddress(request.getReceiverDetailAddress());
	        orders.setTotalPrice(request.getTotalPrice());
	        orders.setMessage(request.getMessage());
	        // DB에 업데이트
			mapper.updateOrder(orders);

		} else {
			// [신규 주문 로직] (기존 번호가 없을 때만 실행)
			orders = new Orders();
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

			// 초기 상태 설정 (결제 대기)
			orders.setOrderStatus("PENDING_PAYMENT");

			// 신규 주문 INSERT (여기서 새 번호가 생성됨)
			mapper.createOrder(orders);
		}

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
		if (order == null)
			throw new Exception("주문 정보가 없습니다.");

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