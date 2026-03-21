package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface RefundMapper {
	int insertRefund(
            @Param("orderNo") Long orderNo,
            @Param("memberNo") Long memberNo,
            @Param("refundReason") String refundReason
    );

    Long getCurrentRefundNo();

    int insertRefundItem(
            @Param("refundNo") Long refundNo,
            @Param("orderItemNo") Long orderItemNo,
            @Param("refundQuantity") Integer refundQuantity,
            @Param("refundAmount") Long refundAmount
    );

    int updateRefundTotalAmount(
            @Param("refundNo") Long refundNo,
            @Param("totalRefundAmount") Long totalRefundAmount
    );

    int increaseRefundedQuantity(
            @Param("orderItemNo") Long orderItemNo,
            @Param("refundQuantity") Integer refundQuantity
    );

    int updateOrderItemStatus(
            @Param("orderItemNo") Long orderItemNo,
            @Param("orderItemStatus") String orderItemStatus
    );

    List<String> findOrderItemStatusesByOrderNo(@Param("orderNo") Long orderNo);

    int updateOrderStatus(
            @Param("orderNo") Long orderNo,
            @Param("orderStatus") String orderStatus
    );
}
