package com.shop.service.admin.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.dto.admin.order.AdminOrderDto;
import com.shop.dto.admin.order.AdminOrderItemDTO;
import com.shop.dto.admin.order.AdminOrderListRequest;
import com.shop.dto.admin.order.AdminOrderListResponse;
import com.shop.dto.admin.order.AdminOrderReadDTO;
import com.shop.dto.admin.order.OrderStatusUpdateRequestDTO;
import com.shop.dto.admin.order.PageResponseDto;
import com.shop.dto.admin.order.RefundStatusUpdateRequestDTO;
import com.shop.mapper.admin.AdminOrderMapper;
import com.shop.mapper.user.OrdersMapper;
import com.shop.service.user.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

	private final AdminOrderMapper adminOrderMapper;
	private final MemberService memberService;
	private final OrdersMapper ordersMapper;

	@Override
	public AdminOrderListResponse getOrderList(AdminOrderListRequest request) throws Exception {
		int totalCount = adminOrderMapper.getOrderCount(request);
		List<AdminOrderDto> orderList = adminOrderMapper.getOrderList(request);

		int totalPage = (int) Math.ceil((double) totalCount / request.getSize());

		int blockSize = 5;
		int startPage = ((request.getPage() - 1) / blockSize) * blockSize + 1;
		int endPage = startPage + blockSize - 1;

		if (endPage > totalPage)
			endPage = totalPage;
		if (totalPage == 0) {
			startPage = 1;
			endPage = 1;
		}

		PageResponseDto pageInfo = new PageResponseDto();
		pageInfo.setCurrentPage(request.getPage());
		pageInfo.setSize(request.getSize());
		pageInfo.setTotalCount(totalCount);
		pageInfo.setTotalPage(totalPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		pageInfo.setHasPrev(startPage > 1);
		pageInfo.setHasNext(endPage < totalPage);

		AdminOrderListResponse response = new AdminOrderListResponse();
		response.setContent(orderList);
		response.setPageInfo(pageInfo);

		return response;
	}

	@Override
	public AdminOrderReadDTO getOrder(Long orderNo) throws Exception {
		// 1. 주문 기본 정보 조회
		AdminOrderReadDTO order = adminOrderMapper.getOrder(orderNo);
		// 2. 주문 상품 목록 조회
		List<AdminOrderItemDTO> items = adminOrderMapper.getOrderItems(orderNo);
		// 3. 주문 DTO에 상품 목록 세팅
		order.setItems(items);

		return order;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderStatus(Long orderNo, OrderStatusUpdateRequestDTO requestDTO) throws Exception {
		String status = requestDTO.getOrderStatus();
		log.info("주문 전체 상태 변경 - 주문번호: {}, 상태: {}", orderNo, status);
		adminOrderMapper.updateOrderStatus(orderNo, status);

		// 주문 전체 상태가 DELIVERED(배송완료)나 CANCELED(취소)로 바뀔 때 등급 갱신
		AdminOrderReadDTO order = adminOrderMapper.getOrder(orderNo);
		if (order != null && order.getMemberNo() != null) {
			updateMemberGradeByAmount(order.getMemberNo());
		}
	}

	// 주문상품 개별 상태 변경
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderItemStatus(Long orderItemNo, String newStatus) throws Exception {
		log.info("주문상품 상태 변경 - orderItemNo: {}, newStatus: {}", orderItemNo, newStatus);

		// 1. 상태 업데이트
		adminOrderMapper.updateOrderItemStatus(orderItemNo, newStatus);

		// 2. 누적 금액 기반 등급 갱신
		// 배송완료, 환불, 취소 시 등급을 재계산합니다.
		if ("DELIVERED".equals(newStatus) || "REFUNDED".equals(newStatus) || "CANCELED".equals(newStatus)) {
			Long memberNo = adminOrderMapper.getMemberNoByOrderItemNo(orderItemNo);
			if (memberNo != null) {
				updateMemberGradeByAmount(memberNo);
				log.info("금액 기반 등급 갱신 완료 - 회원번호: {}", memberNo);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRefundStatus(Long orderNo, RefundStatusUpdateRequestDTO requestDTO) throws Exception {
		String refundStatus = requestDTO.getRefundStatus();
		adminOrderMapper.updateRefundStatus(orderNo, refundStatus);

		// 환불 완료 시 금액 기반 등급 재계산
		if ("COMPLETED".equals(refundStatus)) {
			AdminOrderReadDTO order = adminOrderMapper.getOrder(orderNo);
			if (order != null && order.getMemberNo() != null) {
				updateMemberGradeByAmount(order.getMemberNo());
				log.info("환불 완료 처리 - 회원 {}번 금액 기반 등급 갱신 완료", order.getMemberNo());
			}
		}
	}

	private void updateMemberGradeByAmount(Long memberNo) throws Exception {
		// 1. 배송완료된 총 금액 조회
		long totalAmount = ordersMapper.selectTotalPurchaseAmount(memberNo);

		// 2. 금액별 등급 판별
		String newGrade = "BASIC";
		if (totalAmount >= 1000000)
			newGrade = "VVIP";
		else if (totalAmount >= 500000)
			newGrade = "VIP";
		else if (totalAmount >= 300000)
			newGrade = "GOLD";
		else if (totalAmount >= 100000)
			newGrade = "SILVER";

		// 3. DB 업데이트 호출
		memberService.updateMemberGradeDirectly(memberNo, newGrade);
	}
}
