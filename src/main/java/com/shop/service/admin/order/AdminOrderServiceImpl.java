package com.shop.service.admin.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.dto.admin.order.AdminOrderDto;
import com.shop.dto.admin.order.AdminOrderItemDTO;
import com.shop.dto.admin.order.AdminOrderListRequest;
import com.shop.dto.admin.order.AdminOrderListResponse;
import com.shop.dto.admin.order.AdminOrderReadDTO;
import com.shop.dto.admin.order.OrderStatusUpdateRequestDTO;
import com.shop.dto.admin.order.PageResponseDto;
import com.shop.dto.admin.order.RefundStatusUpdateRequestDTO;
import com.shop.mapper.AdminOrderMapper;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

	@Autowired
	private AdminOrderMapper adminOrderMapper;

	@Override
	public AdminOrderListResponse getOrderList(AdminOrderListRequest request) throws Exception{

		int totalCount = adminOrderMapper.getOrderCount();
		List<AdminOrderDto> orderList = adminOrderMapper.getOrderList(request);

		int totalPage = (int) Math.ceil((double) totalCount / request.getSize());

		// 블록 크기 5
		int blockSize = 5;

		// 예:
		// 1~5 -> startPage=1
		// 6~10 -> startPage=6
		// 11~15 -> startPage=11
		int startPage = ((request.getPage() - 1) / blockSize) * blockSize + 1;
		int endPage = startPage + blockSize - 1;

		// 전체 페이지 수보다 endPage가 크면 잘라냄
		if (endPage > totalPage) {
			endPage = totalPage;
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

	@Override
	public void updateRefundStatus(Long orderNo, RefundStatusUpdateRequestDTO requestDTO) throws Exception {
		adminOrderMapper.updateRefundStatus(orderNo, requestDTO.getRefundStatus());
	}

}
