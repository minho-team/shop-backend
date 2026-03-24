package com.shop.mapper.admin;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.shop.dto.admin.order.AdminOrderDto;
import com.shop.dto.admin.order.AdminOrderItemDTO;
import com.shop.dto.admin.order.AdminOrderListRequest;
import com.shop.dto.admin.order.AdminOrderReadDTO;

// 관리자 주문 관리 DB 접근 Mapper
@Mapper
public interface AdminOrderMapper {

    // 주문 단건 상세 조회
    AdminOrderReadDTO getOrder(Long orderNo) throws Exception;

    // 주문 상품 목록 조회
    List<AdminOrderItemDTO> getOrderItems(Long orderNo) throws Exception;

    // 주문 상태 변경
    void updateOrderStatus(
            @Param("orderNo") Long orderNo,
            @Param("orderStatus") String orderStatus) throws Exception;

    // 환불 상태 변경
    void updateRefundStatus(
            @Param("orderNo") Long orderNo,
            @Param("refundStatus") String refundStatus) throws Exception;

    // 주문 목록 조회 (검색/필터 포함)
    List<AdminOrderDto> getOrderList(AdminOrderListRequest request) throws Exception;

    // 전체 주문 건수 조회 (페이징 계산용)
    int getOrderCount(AdminOrderListRequest request) throws Exception;
}