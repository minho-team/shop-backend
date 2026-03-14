package com.shop.service.admin.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.dto.admin.order.AdminOrderItemDTO;
import com.shop.dto.admin.order.AdminOrderListDTO;
import com.shop.dto.admin.order.AdminOrderReadDTO;
import com.shop.dto.admin.order.AdminOrderSearchDTO;
import com.shop.dto.admin.order.OrderStatusUpdateRequestDTO;
import com.shop.dto.admin.order.RefundStatusUpdateRequestDTO;
import com.shop.mapper.AdminOrderMapper;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

	@Autowired
	private AdminOrderMapper adminOrderMapper;
	
	@Override
	public List<AdminOrderListDTO> getOrderList(AdminOrderSearchDTO searchDTO) throws Exception {
		return adminOrderMapper.getOrderList(searchDTO);
	}

	@Override
	public AdminOrderReadDTO getOrder(Long orderNo) throws Exception {
		//1. 주문 기본 정보 조회
		AdminOrderReadDTO order = adminOrderMapper.getOrder(orderNo);
		//2. 주문 상품 목록 조회
		List<AdminOrderItemDTO> items = adminOrderMapper.getOrderItems(orderNo);
		//3. 주문 DTO에 상품 목록 세팅
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
