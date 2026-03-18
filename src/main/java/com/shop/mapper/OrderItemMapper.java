package com.shop.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.shop.domain.OrderItem;

@Mapper // 마이바티스 매퍼임을 명시
public interface OrderItemMapper {
    // XML의 id와 일치해야 함
    List<OrderItem> selectByOrderNo(Long orderNo);
    
    void insertOrderItem(OrderItem orderItem);
}