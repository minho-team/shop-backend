package com.shop.service.user.order;

import java.util.List;
import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderCreateRequestDTO;
import com.shop.dto.user.order.OrderCreateResponseDTO;
import com.shop.dto.user.order.OrderResponseDTO;
import com.shop.dto.user.order.OrderDetailResponseDTO;
import com.shop.dto.user.order.OrderListRequest; 

public interface OrdersService {

    void createOrder(Orders orders) throws Exception;

    OrderCreateResponseDTO createOrder(OrderCreateRequestDTO request, Long memberNo) throws Exception;
     
    List<Orders> getAllOrders(Long memberNo) throws Exception;
    
    OrderDetailResponseDTO getOrderDetail(Long orderNo) throws Exception;
    
    Orders getOneOrder(Long orderNo);
    
    void cancelOrder(Long orderNo) throws Exception;
    
    void completeRefund(Long orderItemNo) throws Exception;
    
    OrderResponseDTO getMyOrderList(Long memberNo, OrderListRequest request) throws Exception;
}