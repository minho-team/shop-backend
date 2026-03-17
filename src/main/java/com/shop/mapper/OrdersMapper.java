package com.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderDTO;

@Mapper
public interface OrdersMapper {

    void createOrder(Orders orders);
    List<Orders> getAllOrders(Long memberNo); 
    Orders getOneOrder(Long orderNo);

    // --- 마이페이지 페이징 전용 메서드 ---

    // 1. 전체 개수 조회
    int getTotalCount(@Param("memberNo") Long memberNo);

    // 2. 10개씩 끊어오기
    List<OrderDTO> getMyOrderList(
        @Param("memberNo") Long memberNo, 
        @Param("startRow") int startRow, 
        @Param("endRow") int endRow
    );
}