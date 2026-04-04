package com.shop.mapper.user;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderDTO;
import com.shop.dto.user.order.OrderItemDTO;
import com.shop.dto.user.order.OrderListRequest;

// 사용자 주문 DB 접근 Mapper
// 관리자용 주문 조회는 AdminOrdersMapper로 분리
@Mapper
public interface OrdersMapper {

	// --- 주문 생성 및 수정 ---
    void createOrder(Orders orders);
    void updateOrder(Orders orders);
    void updatePgOrderId(@Param("orderNo") Long orderNo, @Param("pgOrderId") String pgOrderId);

    // --- 주문 조회 ---
    List<Orders> getAllOrders(Long memberNo);
    Orders getOneOrder(Long orderNo);
    Orders findByPgOrderId(String pgOrderId);
    
    // --- 마이페이지 및 페이징 ---
    List<OrderItemDTO> getOrderItemList(Long orderNo);
    int getTotalCount(@Param("memberNo") Long memberNo, @Param("req") OrderListRequest req);
    List<OrderDTO> getMyOrderList(@Param("memberNo") Long memberNo, @Param("req") OrderListRequest request);

    // --- 상태 변경 및 재고 관리 ---
    void updateOrderStatus(@Param("orderNo") Long orderNo, @Param("orderStatus") String orderStatus);
    void increaseProductStock(@Param("productOptionNo") Long productOptionNo, @Param("quantity") Integer quantity);

    // 특정 회원의 배송완료(DELIVERED)된 총 주문 금액 합산
    long selectTotalPurchaseAmount(@Param("memberNo") Long memberNo);

    // 주문 상품 번호(orderItemNo)로 부모 주문 번호(orderNo) 조회
    // (환불 시 해당 주문의 상태를 변경하여 금액을 차감하기 위함)
    Long getOrderNoByItemNo(@Param("orderItemNo") Long orderItemNo);
    

    
    
}