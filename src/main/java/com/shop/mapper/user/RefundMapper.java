package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface RefundMapper {
	int insertRefund(@Param("orderNo") Long orderNo, @Param("memberNo") Long memberNo,
			@Param("refundReason") String refundReason);

	Long getCurrentRefundNo();

	int insertRefundItem(@Param("refundNo") Long refundNo, @Param("orderItemNo") Long orderItemNo,
			@Param("refundQuantity") Integer refundQuantity, @Param("refundAmount") Long refundAmount);

	int updateRefundTotalAmount(@Param("refundNo") Long refundNo, @Param("totalRefundAmount") Long totalRefundAmount);

	int increaseRefundedQuantity(@Param("orderItemNo") Long orderItemNo,
			@Param("refundQuantity") Integer refundQuantity);

	int updateOrderItemStatus(@Param("orderItemNo") Long orderItemNo, @Param("orderItemStatus") String orderItemStatus);

	List<String> findOrderItemStatusesByOrderNo(@Param("orderNo") Long orderNo);

	int updateOrderStatus(@Param("orderNo") Long orderNo, @Param("orderStatus") String orderStatus);

	// refundNo로 orderNo 조회
	Long getOrderNoByRefundNo(Long refundNo) throws Exception;

	// 환불 테이블 상태 변경
	void updateRefundStatus(Long refundNo, String status) throws Exception;

	// 환불_item 테이블 상태 변경
	void updateRefundItemStatus(Long refundNo, String status) throws Exception;

	// 프론트의 환불 금액은 신뢰하지 않고 db의 값을 신뢰하기 위한 매퍼
	Long getRefundAmountByRefundNo(Long refundNo) throws Exception;

	// 환불 승인 시간 기록
	void updateApprovedAt(Long refundNo) throws Exception;

	// 환불 완료 시간 기록
	void updateCompletedAt(Long refundNo) throws Exception;
	
	// 환불 거절 시간 기록
	void updateRejectedAt(Long refundNo) throws Exception;

	//refundNo로 order_item_no 가져오기
	Long getOrderItemNoByRefundNo(Long refundNo) throws Exception;

	//refundNo로 refund_item의 수량 가져오기
	Long getRefundQuantityByRefundNo(Long refundNo) throws Exception;
}
