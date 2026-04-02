package com.shop.dto.user.order;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderListResponse {
    // 실제 주문 목록 데이터
    private List<OrderDTO> content; 
    // 페이징 정보
    private PageResponseDto pageInfo;
}