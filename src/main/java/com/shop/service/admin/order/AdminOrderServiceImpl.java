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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // [추가]log.info 를 위해 추가했음
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
	public void updateOrderStatus(Long orderNo, OrderStatusUpdateRequestDTO requestDTO) throws Exception {
		adminOrderMapper.updateOrderStatus(orderNo, requestDTO.getOrderStatus());
	}

	@Override // [추가] updateRefundStatus 전체코드 수정했음
	@Transactional(rollbackFor = Exception.class) // [추가] 데이터 정합성 보장
	public void updateRefundStatus(Long orderNo, RefundStatusUpdateRequestDTO requestDTO) throws Exception {
	    // 1. 기존 환불 상태 업데이트 로직
	    adminOrderMapper.updateRefundStatus(orderNo, requestDTO.getRefundStatus());

	    // 2. [추가] 환불 완료(REFUNDED) 시 구매 횟수 차감 로직
	    if ("REFUNDED".equals(requestDTO.getRefundStatus())) {
	        // 주문 정보 조회를 통해 memberNo 획득
	        AdminOrderReadDTO order = adminOrderMapper.getOrder(orderNo);

	        if (order != null && order.getMemberNo() != null) {
	            Long memberNo = order.getMemberNo();

	            // 구매 횟수 1 차감 (purchase_count - 1)
	            memberService.decreasePurchaseCount(memberNo);

	            // 바뀐 횟수를 기준으로 회원 등급 재계산
	            memberService.updateMemberGrade(memberNo);

	            log.info("환불 완료 처리: 회원 {}번 구매 횟수 차감 및 등급 재계산 완료", memberNo);
	        }
	    }
	}
}
