package com.shop.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.dto.admin.order.AdminOrderItemDetailResponseDTO;

@Mapper
public interface AdminOrderItemMapper {

	List<AdminOrderItemDetailResponseDTO> getAdminOrderItemList(Long orderNo) throws Exception;

	int updateOrderItemStatus(Long orderItemNo, String orderItemStatus) throws Exception;

	 Long findOrderNoByOrderItemNo(@Param("orderItemNo") Long orderItemNo);

	    List<String> findOrderItemStatusesByOrderNo(@Param("orderNo") Long orderNo);

	    int updateOrderStatus(
	            @Param("orderNo") Long orderNo,
	            @Param("orderStatus") String orderStatus
	    );
	    
	    // 변경 전 현재 상태 조회 (중복 DELIVERED 방지용)
	    String getOrderItemStatus(@Param("orderItemNo") Long orderItemNo);
	    
	    // orderItemNo로 memberNo 조회 (purchaseCount 증가용)
	    Long getMemberNoByOrderItemNo(@Param("orderItemNo") Long orderItemNo);

}
