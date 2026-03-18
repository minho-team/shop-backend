package com.shop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderDTO;

@Mapper
public interface OrdersMapper {

    // 주문 생성
    void createOrder(Orders orders);

    // 특정 회원 전체 주문 조회
    List<Orders> getAllOrders(Long memberNo);

    // 주문 단건 조회
    Orders getOneOrder(Long orderNo);

    // 특정 회원 주문 전체 건수
    int getTotalCount(@Param("memberNo") Long memberNo);

    // 내 주문 페이징 조회 (마이페이지용)
    List<OrderDTO> getMyOrderList(
        @Param("memberNo") Long memberNo,
        @Param("startRow") int startRow,
        @Param("endRow") int endRow
    );

    // 관리자 - 특정 회원 최근 주문 5건 (요약용)
    List<Orders> selectRecentOrdersByMemberNo(Long memberNo);

    // ================================================
    // 관리자 - 특정 회원 주문 전체 페이징 조회 (5개씩)
    // ================================================
    List<Orders> selectOrderPageByMemberNo(
            @Param("memberNo") Long memberNo,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow);

    // ================================================
    // 관리자 - 특정 회원 주문 전체 건수 (페이징 계산용)
    // ================================================
    int countOrdersByMemberNo(Long memberNo);
}	