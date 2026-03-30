package com.shop.service.admin.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // [추가]임포트 추가

import com.shop.dto.admin.order.AdminOrderDto;
import com.shop.dto.admin.order.AdminOrderItemDTO;
import com.shop.dto.admin.order.AdminOrderListRequest;
import com.shop.dto.admin.order.AdminOrderListResponse;
import com.shop.dto.admin.order.AdminOrderReadDTO;
import com.shop.dto.admin.order.OrderStatusUpdateRequestDTO;
import com.shop.dto.admin.order.PageResponseDto;
import com.shop.dto.admin.order.RefundStatusUpdateRequestDTO;
import com.shop.mapper.admin.AdminOrderMapper;
import com.shop.service.user.member.MemberService; // [추가]임포트 추가

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // [추가]log.info 를 위해 추가했음
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

	@Autowired
	private AdminOrderMapper adminOrderMapper;
	
	@Autowired
    private MemberService memberService; // [추가] 회원 서비스 주입

	 @Override
	    public AdminOrderListResponse getOrderList(AdminOrderListRequest request) throws Exception {

	        int totalCount = adminOrderMapper.getOrderCount(request);
	        List<AdminOrderDto> orderList = adminOrderMapper.getOrderList(request);

	        int totalPage = (int) Math.ceil((double) totalCount / request.getSize());

	        int blockSize = 5;
	        int startPage = ((request.getPage() - 1) / blockSize) * blockSize + 1;
	        int endPage = startPage + blockSize - 1;

	        if (endPage > totalPage) {
	            endPage = totalPage;
	        }

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
	}
	
	// 주문상품 개별 상태 변경
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderItemStatus(Long orderItemNo, String newStatus) throws Exception {
		log.info("주문상품 상태 변경 - orderItemNo: {}, newStatus: {}", orderItemNo, newStatus);
 
		if ("DELIVERED".equals(newStatus)) {
			// 1. 변경 전 현재 상태 조회 (중복 방지용)
			String currentStatus = adminOrderMapper.getOrderItemStatus(orderItemNo);
			log.info("현재 상태: {} → 변경할 상태: {}", currentStatus, newStatus);
 
			// 2. 상태 업데이트
			adminOrderMapper.updateOrderItemStatus(orderItemNo, newStatus);
 
			// 3. 이미 DELIVERED 였으면 카운트 올리지 않음
			if ("DELIVERED".equals(currentStatus)) {
				log.warn("이미 DELIVERED 상태입니다. purchaseCount 증가 건너뜀 - orderItemNo: {}", orderItemNo);
				return;
			}
 
			// 4. 처음 DELIVERED 로 바뀌는 경우에만 카운트 +1, 등급 갱신
			Long memberNo = adminOrderMapper.getMemberNoByOrderItemNo(orderItemNo);
			if (memberNo != null) {
				memberService.increasePurchaseCount(memberNo);
				log.info("purchaseCount +1, grade 갱신 완료 - 회원번호: {}, orderItemNo: {}", memberNo, orderItemNo);
			} else {
				log.warn("memberNo 조회 실패 - orderItemNo: {}", orderItemNo);
			}
 
		} else {
			// DELIVERED 가 아닌 다른 상태 변경은 그냥 업데이트만
			adminOrderMapper.updateOrderItemStatus(orderItemNo, newStatus);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRefundStatus(Long orderNo, RefundStatusUpdateRequestDTO requestDTO) throws Exception {
		String refundStatus = requestDTO.getRefundStatus();
		adminOrderMapper.updateRefundStatus(orderNo, refundStatus);
 
		if ("COMPLETED".equals(refundStatus)) {
			AdminOrderReadDTO order = adminOrderMapper.getOrder(orderNo);
			if (order != null && order.getMemberNo() != null) {
				memberService.decreasePurchaseCount(order.getMemberNo());
				memberService.updateMemberGrade(order.getMemberNo());
				log.info("환불 완료 - 회원 {}번 purchaseCount-1, grade 갱신 완료", order.getMemberNo());
			}
		}
	}
}
