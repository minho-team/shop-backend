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
    
    // 주문 상품 상태 변경 (배송 완료 시 리뷰 권한 활성화용)
    void updateOrderItemStatus(
    		@Param("orderItemNo") Long orderItemNo,
            @Param("status") String status) throws Exception;
    
    // 주문상품의 현재 상태 조회 (중복 DELIVERED 방지용)
    String getOrderItemStatus(@Param("orderItemNo") Long orderItemNo);

    // 환불 상태 변경
    void updateRefundStatus(
            @Param("orderNo") Long orderNo,
            @Param("refundStatus") String refundStatus) throws Exception;

    // 주문 목록 조회 (검색/필터 포함)
    List<AdminOrderDto> getOrderList(AdminOrderListRequest request) throws Exception;

    // 전체 주문 건수 조회 (페이징 계산용)
    int getOrderCount(AdminOrderListRequest request) throws Exception;
    
    
    // 주문 상품 번호(Order Item No)를 기반으로 해당 주문을 수행한 회원 번호(Member No)를 조회합니다.
    // 배송 완료 처리 시 회원의 구매 횟수를 증가시키기 위한 식별자 확보 용도입니다.
    Long getMemberNoByOrderItemNo(@Param("orderItemNo") Long orderItemNo);
}