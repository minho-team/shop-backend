package com.shop.dto.user.order;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long orderNo;
    private String mainImageUrl;
    private String mainProductName;
    private int totalQuantity;
    private String orderStatus;
    private Long totalPrice;
    private LocalDateTime createdAt;
}