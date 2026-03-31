package com.shop.mapper.user;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderDTO;
import com.shop.dto.user.order.OrderItemDTO;

// 사용자 주문 DB 접근 Mapper
// 관리자용 주문 조회는 AdminOrdersMapper로 분리
@Mapper
public interface OrdersMapper {

    // 주문 생성
    void createOrder(Orders orders);
    
    // 기존 주문 정보 업데이트
    void updateOrder(Orders orders);

    // 특정 회원 전체 주문 조회
    List<Orders> getAllOrders(Long memberNo);

    // 주문 단건 조회
    Orders getOneOrder(Long orderNo);

    // 주문 상품 목록 조회
    List<OrderItemDTO> getOrderItemList(Long orderNo);

    // 특정 회원 주문 전체 건수 (마이페이지 페이징 계산용)
    int getTotalCount(@Param("memberNo") Long memberNo);

    // 내 주문 페이징 조회 (마이페이지용)
    List<OrderDTO> getMyOrderList(
            @Param("memberNo") Long memberNo,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow);

    // pg_order_id로 주문 조회 (결제 완료 콜백 처리용)
    Orders findByPgOrderId(String pgOrderId);

    // pg_order_id 업데이트 (결제 요청 시)
    void updatePgOrderId(
            @Param("orderNo") Long orderNo,
            @Param("pgOrderId") String pgOrderId);

    // 주문 상태 변경
    void updateOrderStatus(
            @Param("orderNo") Long orderNo,
            @Param("orderStatus") String orderStatus);
    
    void increaseProductStock(@Param("productOptionNo") Long productOptionNo, 
            @Param("quantity") Integer quantity);
    
}