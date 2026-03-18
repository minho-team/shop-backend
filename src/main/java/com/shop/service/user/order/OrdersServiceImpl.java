package com.shop.service.user.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.OrderItem;
import com.shop.domain.Orders;
import com.shop.dto.user.order.OrderDetailResponseDTO;
import com.shop.dto.user.order.OrderResponseDTO;
import com.shop.mapper.OrdersMapper;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper mapper;

    // 1. 주문 생성
    @Override
    @Transactional
    public void createOrder(Orders orders) {
        mapper.createOrder(orders);
    }

    // 2. 마이페이지 주문 목록 조회 (페이징 처리)
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getMyOrderList(Long memberNo, int page) {
        int size = 10;
        int startRow = (page - 1) * size + 1;
        int endRow = page * size;
        
        // 10개씩 끊어서 가져오기
        List<com.shop.dto.user.order.OrderDTO> list = mapper.getMyOrderList(memberNo, startRow, endRow);
        // 전체 주문 개수 가져오기
        int totalCount = mapper.getTotalCount(memberNo);
        
        return new OrderResponseDTO(list, totalCount, page, size);
    }

    // 3. [상세조회 핵심] 주문 정보와 상품 상세 리스트를 DTO에 담아서 반환
    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO getOrderDetail(Long orderNo) throws Exception {
        // 주문 마스터 정보 (Orders)
        Orders order = mapper.getOneOrder(orderNo);
        // 주문 상세 상품들 (List<OrderItem>)
        List<OrderItem> items = mapper.getOrderItemList(orderNo); 
        
        return OrderDetailResponseDTO.builder()
                .order(order)
                .items(items)
                .build();
    }

    // 4. 단건 조회 
    @Override
    @Transactional(readOnly = true)
    public Orders getOneOrder(Long orderNo) {
        return mapper.getOneOrder(orderNo);
    }

    // 5. [수정 완료] 전체 주문 내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<Orders> getAllOrders(Long memberNo) throws Exception {
        // null 대신 매퍼 호출로 변경!
        return mapper.getAllOrders(memberNo);
    }
}