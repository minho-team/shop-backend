package com.shop.service.admin.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.shop.dto.admin.order.AdminOrderItemDetailResponseDTO;
import com.shop.mapper.admin.AdminOrderItemMapper;
import com.shop.service.user.member.MemberService;
 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminOrderItemServiceImpl implements AdminOrderItemService {

	private final AdminOrderItemMapper adminOrderItemMapper;
	private final MemberService memberService; 

	@Override
	public List<AdminOrderItemDetailResponseDTO> getOrderItemList(Long orderNo) throws Exception {
		List<AdminOrderItemDetailResponseDTO> list = adminOrderItemMapper.getAdminOrderItemList(orderNo);
		if (list != null) {
			for (AdminOrderItemDetailResponseDTO item : list) {
				if (item.getImageUrl() != null && !item.getImageUrl().isBlank()) {
					item.setImageUrl("/upload/" + item.getImageUrl());
				}
			}
		}

		return list;
	}
	// 주문상품 개별 상태 변경
	@Override
	@Transactional
	public void updateOrderItemStatus(Long orderItemNo, String orderItemStatus) throws Exception {
 
		log.info("주문상품 상태 변경 - orderItemNo: {}, newStatus: {}", orderItemNo, orderItemStatus);
 
		// 1. 변경 전 현재 상태 조회 (중복 DELIVERED 방지)
		String currentStatus = adminOrderItemMapper.getOrderItemStatus(orderItemNo);
		log.info("현재 상태: {} → 변경할 상태: {}", currentStatus, orderItemStatus);
 
		// 2. 상태 업데이트
		int updatedCount = adminOrderItemMapper.updateOrderItemStatus(orderItemNo, orderItemStatus);
		if (updatedCount == 0) {
			throw new RuntimeException("해당 주문상품이 없습니다.");
		}
 
		// 3. DELIVERED 로 바뀌는 경우에만 purchaseCount 처리
		if ("DELIVERED".equals(orderItemStatus)) {
			if ("DELIVERED".equals(currentStatus)) {
				log.warn("이미 DELIVERED 상태입니다. purchaseCount 증가 건너뜀 - orderItemNo: {}", orderItemNo);
			} else {
				Long memberNo = adminOrderItemMapper.getMemberNoByOrderItemNo(orderItemNo);
				if (memberNo != null) {
					memberService.increasePurchaseCount(memberNo);
					log.info("purchaseCount +1, grade 갱신 완료 - 회원번호: {}, orderItemNo: {}", memberNo, orderItemNo);
				} else {
					log.warn("memberNo 조회 실패 - orderItemNo: {}", orderItemNo);
				}
			}
		}
		
		// 4. 주문 전체 상태 자동 동기화 
		Long orderNo = adminOrderItemMapper.findOrderNoByOrderItemNo(orderItemNo);
		if (orderNo == null) {
			throw new RuntimeException("주문번호를 찾을 수 없습니다.");
		}
		List<String> orderItemStatuses = adminOrderItemMapper.findOrderItemStatusesByOrderNo(orderNo);
		String calculatedOrderStatus = calculateOrderStatus(orderItemStatuses);
		adminOrderItemMapper.updateOrderStatus(orderNo, calculatedOrderStatus);
		log.info("주문 전체 상태 동기화 완료 - orderNo: {}, orderStatus: {}", orderNo, calculatedOrderStatus);
	}

	// 주문서의 아이템에 대해서 각자의 상태가 있기 때문에 이에 대한 order_state 동기화 정책이 필요함
	//주문서의 아이템이
	//	전부 PENDING_PAYMENT → PENDING_PAYMENT
	//	전부 CANCELED → CANCELED
	//	전부 DELIVERED → DELIVERED
	//	하나라도 SHIPPING 있으면 → SHIPPING
	//	하나라도 PREPARING 있으면 → PREPARING
	//	그 외 애매한 섞임 → PAYMENT_COMPLETED
	private String calculateOrderStatus(List<String> statuses) {
		if (statuses == null || statuses.isEmpty()) {
			throw new RuntimeException("주문상품 상태가 없습니다.");
		}

		if (statuses.stream().allMatch(status -> "PENDING_PAYMENT".equals(status))) {
			return "PENDING_PAYMENT";
		}

		if (statuses.stream().allMatch(status -> "CANCELED".equals(status))) {
			return "CANCELED";
		}

		if (statuses.stream().allMatch(status -> "DELIVERED".equals(status))) {
			return "DELIVERED";
		}

		if (statuses.stream().anyMatch(status -> "SHIPPING".equals(status))) {
			return "SHIPPING";
		}

		if (statuses.stream().anyMatch(status -> "PREPARING".equals(status))) {
			return "PREPARING";
		}

		return "PAYMENT_COMPLETED";
	}

}
