package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.dto.admin.order.AdminOrderItemDTO;
import com.shop.dto.admin.order.AdminOrderListDTO;
import com.shop.dto.admin.order.AdminOrderReadDTO;
import com.shop.dto.admin.order.AdminOrderSearchDTO;

@Mapper
public interface AdminOrderMapper {

	List<AdminOrderListDTO> getOrderList(AdminOrderSearchDTO searchDTO) throws Exception;

	AdminOrderReadDTO getOrder(Long orderNo) throws Exception;
	
	List<AdminOrderItemDTO> getOrderItems(Long orderNo) throws Exception;

	void updateOrderStatus(@Param("orderNo") Long orderNo,
            @Param("orderStatus") String orderStatus) throws Exception;

	void updateRefundStatus(@Param("orderNo") Long orderNo,
            @Param("refundStatus") String refundStatus) throws Exception;

}
