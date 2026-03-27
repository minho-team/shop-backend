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
	    
	    // 로그 1: 상태값 확인
	    log.info("==== 주문 상태 변경 트리거 시작 ====");
	    log.info("주문번호: {}, 요청된 상태값: [{}]", orderNo, status);

	    // 1. 상태 업데이트 실행
	    adminOrderMapper.updateOrderStatus(orderNo, status);

	    // 로그 2: 조건문 진입 시도 (대소문자 무관하게 비교)
	    if ("DELIVERED".equalsIgnoreCase(status)) {
	        log.info(">>> [조건 일치] '배송완료' 로직을 실행합니다.");

	        // 2. 주문 정보 가져오기 (회원번호 확인용)
	        AdminOrderReadDTO order = adminOrderMapper.getOrder(orderNo);
	        
	        if (order != null) {
	            log.info(">>> 조회된 주문 정보 - 회원번호(memberNo): {}", order.getMemberNo());

	            if (order.getMemberNo() != null) {
	                // 3. 구매 횟수 증가 및 등급 갱신
	                memberService.increasePurchaseCount(order.getMemberNo());
	                memberService.updateMemberGrade(order.getMemberNo());
	                log.info(">>> [성공] 회원 {}번의 카운트와 등급이 갱신되었습니다.", order.getMemberNo());
	            } else {
	                log.error(">>> [오류] memberNo가 null입니다. XML의 getOrder 쿼리를 확인하세요.");
	            }
	        } else {
	            log.error(">>> [오류] 해당 주문번호({})의 정보를 조회할 수 없습니다.", orderNo);
	        }
	    } else {
	        log.info(">>> [조건 불일치] 현재 상태가 DELIVERED가 아니므로 카운트를 올리지 않습니다.");
	    }
	    log.info("==== 주문 상태 변경 트리거 종료 ====");
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderItemStatus(Long orderItemNo, String status) throws Exception {
	    // 디버깅을 위한 상태 출력 
	    System.out.println("orderItemNo = " + orderItemNo);
	    System.out.println("orderItemStatus = " + status);

	    // 1. 주문 상품의 상태를 DB에 업데이트 (주문 상품 단위 상태 변경)
	    adminOrderMapper.updateOrderItemStatus(orderItemNo, status);

	    // 2. 비즈니스 로직: 상품 상태가 'DELIVERED'(배송 완료)로 변경되는 시점에 수행
	    if ("DELIVERED".equals(status)) {
	        // 주문 상품 번호를 역추적하여 해당 주문의 주인(회원) 번호를 획득
	        Long memberNo = adminOrderMapper.getMemberNoByOrderItemNo(orderItemNo);
	        
	        if (memberNo != null) {
	            // 획득한 회원 번호로 누적 구매 횟수(Purchase Count)를 1 증가
	            memberService.increasePurchaseCount(memberNo);
	            
	            // 변경된 구매 횟수를 기준으로 회원의 등급(Grade)을 재판정하여 업데이트
	            memberService.updateMemberGrade(memberNo);
	            
	            log.info("상품 배송완료 반영 성공 - 회원 번호: {}, 상품 번호: {}", memberNo, orderItemNo);
	        } else {
	            log.warn("회원 번호를 찾을 수 없습니다. 상품 번호: {}", orderItemNo);
	        }
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
	            log.info("환불 완료로 인한 구매 횟수 차감 완료: 회원번호 {}", order.getMemberNo());
	        }
	    }
	}
}
