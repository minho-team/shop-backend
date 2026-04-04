package com.shop.service.user.order;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.Member;
import com.shop.domain.OrderItem;
import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderCreateRequestDTO;
import com.shop.dto.user.order.OrderCreateResponseDTO;
import com.shop.dto.user.order.OrderDTO;
import com.shop.dto.user.order.OrderDetailResponseDTO;
import com.shop.dto.user.order.OrderItemCreateRequestDTO;
import com.shop.dto.user.order.OrderItemDTO;
import com.shop.dto.user.order.OrderListRequest;
import com.shop.dto.user.order.OrderResponseDTO;
import com.shop.mapper.user.OrderItemMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.mapper.user.ReviewMapper;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersServiceImpl implements OrdersService {

	private final OrdersMapper mapper;
	private final OrdersMapper ordersMapper;
	private final OrderItemMapper orderItemMapper;
	private final MemberService memberService;
	private final ReviewMapper reviewMapper;

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
		if (memberNo != null) {
			updateMemberGradeByAmount(memberNo); 
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
	public OrderResponseDTO getMyOrderList(Long memberNo, OrderListRequest request) throws Exception {

		log.info("orders서비스의 getMyOrderList 진입 - 검색어: {}", request.getKeyword());

		// [해결] 호출 대상을 'ordersMapper'에서 필드명인 'mapper'로 수정함
		// 1. 검색 조건이 포함된 전체 데이터 개수 조회 (페이징 계산용)
		int totalCount = mapper.getTotalCount(memberNo, request);

		// 2. 검색 조건 및 페이징(offset, size)이 포함된 실제 리스트 조회
		List<OrderDTO> orderList = mapper.getMyOrderList(memberNo, request);

		log.info("조회된 주문 건수: {}", orderList.size());

		if (orderList != null) {
			for (OrderDTO dto : orderList) {
				if (dto.getMainImageUrl() != null && !dto.getMainImageUrl().isBlank()) {
					dto.setMainImageUrl("/upload/" + dto.getMainImageUrl());
				}
			}
		}

		// 3. 사용자님이 만드신 OrderResponseDTO 생성자를 호출하여 페이징 계산 및 반환
		// 생성자 순서: (리스트, 전체개수, 현재페이지, 페이지당개수)
		return new OrderResponseDTO(orderList, totalCount, request.getPage(), request.getSize());
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

	private void updateMemberGradeByAmount(Long memberNo) throws Exception {
	    // 로그 1: 파라미터 확인
	    log.info("==> 등급 재계산 시작 - 회원번호: {}", memberNo);
	    
	    long totalAmount = ordersMapper.selectTotalPurchaseAmount(memberNo);
	    
	    // 로그 2: 쿼리 결과 확인
	    log.info("==> DB 조회 결과(누적금액): {}원", totalAmount);

	    String newGrade = "BASIC";
	    if (totalAmount >= 1000000)      newGrade = "VVIP";
	    else if (totalAmount >= 500000)  newGrade = "VIP";
	    else if (totalAmount >= 300000)  newGrade = "GOLD";
	    else if (totalAmount >= 100000)  newGrade = "SILVER";

	    log.info("==> 판별된 등급: {}", newGrade);
	    memberService.updateMemberGradeDirectly(memberNo, newGrade);
	}

	
	/**
	 * 관리자: 배송 완료 처리 (상향 트리거)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void completeDelivery(Long orderNo) throws Exception {
		Orders order = mapper.getOneOrder(orderNo);
		mapper.updateOrderStatus(orderNo, "DELIVERED");

		// 누적 금액 재계산 및 등급 업데이트
		updateMemberGradeByAmount(order.getMemberNo());
	}

	/**
	 * 관리자: 환불 완료 처리 (하향 트리거)
	 */

	@Override
    @Transactional(rollbackFor = Exception.class)
    public void completeRefund(Long orderItemNo) throws Exception {
        // 1. 환불 상태 변경 및 리뷰 삭제
        orderItemMapper.updateSingleOrderItemStatus(orderItemNo, "REFUNDED");
        reviewMapper.deleteReviewByOrderItemNo(orderItemNo);

        // 2. 환불된 상품이 속한 주문(Orders)의 정보 조회
        Long orderNo = orderItemMapper.getOrderNoByItemNo(orderItemNo);
        Orders order = mapper.getOneOrder(orderNo);

        // 3. 주문 상태를 CANCELED로 변경하여 누적 금액(SUM)에서 제외
        mapper.updateOrderStatus(orderNo, "CANCELED");

        // 4. [핵심] 누적 금액 하락에 따른 등급 재계산 호출 (하향 조정)
        updateMemberGradeByAmount(order.getMemberNo());

        log.info("환불 완료 처리 및 회원({}) 등급 재산정 완료", order.getMemberNo());
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
		Orders order = mapper.getOneOrder(orderNo);
		if (order == null)
			throw new Exception("주문 정보가 없습니다.");

		if (!"PENDING_PAYMENT".equals(order.getOrderStatus())) {
			throw new Exception("결제대기 상태의 주문만 취소가 가능합니다.");
		}

		// 1. 상태 변경
		mapper.updateOrderStatus(orderNo, "CANCELED");
		orderItemMapper.updateOrderItemStatusByOrderNo(orderNo, "CANCELED");

		// 2. 재고 복구
		List<OrderItemDTO> items = orderItemMapper.selectOrderItemsByOrderNo(orderNo);
		for (OrderItemDTO item : items) {
			mapper.increaseProductStock(item.getProductOptionNo(), item.getQuantity());
		}

		// 3. 등급 재계산 (취소된 주문은 SUM에서 제외됨)
		updateMemberGradeByAmount(order.getMemberNo());
	}
	
	/**
     * 도메인 객체(Orders)를 직접 전달받아 주문을 생성하고 등급을 갱신합니다.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(Orders orders) throws Exception {
        // 1. 주문 기본 정보 DB 저장 (OrdersMapper 호출)
        mapper.createOrder(orders);

        // 2. 저장된 주문 정보를 바탕으로 누적 금액 기반 등급 갱신 호출
        if (orders.getMemberNo() != null) {
            updateMemberGradeByAmount(orders.getMemberNo());
            log.info("(OrdersService) 도메인 기반 주문 저장 완료 -> 회원 {}번 등급 재산정 호출", orders.getMemberNo());
        }
    }

}