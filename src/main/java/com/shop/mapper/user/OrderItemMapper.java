package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.OrderItem;
import com.shop.dto.user.order.OrderItemDTO;
import com.shop.dto.user.payment.PaymentPrepareItemDTO;

@Mapper
public interface OrderItemMapper {
    
    List<OrderItem> selectByOrderNo(Long orderNo);
    
    void insertOrderItem(OrderItem orderItem);
    
    void deleteByOrderNo(Long orderNo);
    
    //orderNo로 order_item의 상태 변경
    void updateOrderItemStatusByOrderNo(@Param("orderNo") Long orderNo, @Param("orderItemStatus") String orderItemStatus);
    
    void updateOrderItemStatusByOrderItemNo(@Param("orderItemNo") Long orderItemNo, @Param("orderItemStatus") String orderItemStatus);
    
    List<PaymentPrepareItemDTO> selectPaymentResultItemsByOrderNo(Long orderNo);
    
    List<OrderItemDTO> selectOrderItemsByOrderNo(Long orderNo);
    
    void updateSingleOrderItemStatus(@Param("orderItemNo") Long orderItemNo, @Param("orderItemStatus") String orderItemStatus);
    
    Long getOrderNoByItemNo(@Param("orderItemNo") Long orderItemNo);

}







