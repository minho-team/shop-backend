package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.OrderItem;
import com.shop.dto.user.payment.PaymentPrepareItemDTO;

@Mapper
public interface OrderItemMapper {
    
    List<OrderItem> selectByOrderNo(Long orderNo);
    
    void insertOrderItem(OrderItem orderItem);
    
    void updateOrderItemStatusByOrderNo(@Param("orderNo") Long orderNo, @Param("orderItemStatus") String orderItemStatus);
    
    List<PaymentPrepareItemDTO> selectPaymentResultItemsByOrderNo(Long orderNo);
}