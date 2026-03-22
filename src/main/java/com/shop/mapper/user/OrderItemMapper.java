package com.shop.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.shop.domain.OrderItem;

@Mapper
public interface OrderItemMapper {
    
    List<OrderItem> selectByOrderNo(Long orderNo);
    
    void insertOrderItem(OrderItem orderItem);
    
    void updateOrderItemStatusByOrderNo(@Param("orderNo") Long orderNo, @Param("orderItemStatus") String orderItemStatus);
}