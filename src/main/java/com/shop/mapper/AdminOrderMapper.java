package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.dto.admin.order.AdminOrderDto;
import com.shop.dto.admin.order.AdminOrderItemDTO;
import com.shop.dto.admin.order.AdminOrderListRequest;
import com.shop.dto.admin.order.AdminOrderReadDTO;

@Mapper
public interface AdminOrderMapper {

	AdminOrderReadDTO getOrder(Long orderNo) throws Exception;

	List<AdminOrderItemDTO> getOrderItems(Long orderNo) throws Exception;

	void updateOrderStatus(@Param("orderNo") Long orderNo, @Param("orderStatus") String orderStatus) throws Exception;

	void updateRefundStatus(@Param("orderNo") Long orderNo, @Param("refundStatus") String refundStatus)
			throws Exception;

	List<AdminOrderDto> getOrderList(AdminOrderListRequest request) throws Exception;

	int getOrderCount() throws Exception;

}
