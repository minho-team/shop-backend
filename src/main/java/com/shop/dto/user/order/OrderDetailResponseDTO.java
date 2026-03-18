package com.shop.dto.user.order;

import java.util.List;
import com.shop.domain.Orders;
import com.shop.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponseDTO {
    private Orders order;
    
    private List<OrderItem> items;
}