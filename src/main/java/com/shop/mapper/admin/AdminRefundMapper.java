package com.shop.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.dto.admin.refund.AdminRefundDetailFlatRowDTO;
import com.shop.dto.admin.refund.AdminRefundFlatRowDTO;
import com.shop.dto.admin.refund.AdminRefundListRequestDTO;

@Mapper
public interface AdminRefundMapper {

	int getRefundCount(AdminRefundListRequestDTO request);

	List<AdminRefundFlatRowDTO> getRefundList(AdminRefundListRequestDTO request);

	List<AdminRefundDetailFlatRowDTO> getRefundDetail(@Param("refundNo") Long refundNo);

	int updateRefundHeaderStatus(@Param("refundNo") Long refundNo, @Param("refundStatus") String refundStatus);

	int updateRefundItemsStatus(@Param("refundNo") Long refundNo, @Param("refundStatus") String refundStatus);

	int countNotCompletedRefundItems(@Param("refundNo") Long refundNo);

	int existsRefund(@Param("refundNo") Long refundNo);

	int updateOrderItemsStatusByRefundNo(@Param("refundNo") Long refundNo,
			@Param("orderItemStatus") String orderItemStatus);
	
	void updateRefundHeaderTime(@Param("refundNo") Long refundNo, @Param("refundStatus") String refundStatus);
}