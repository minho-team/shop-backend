package com.shop.dto.user.product;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomeProductCardDto {
    private Long productNo;
    private String name;
    private Integer price;
    private Integer discountRate;
    private String imageUrl;
    private String sameDayDeliveryYn;
    private LocalDateTime createdAt;
}