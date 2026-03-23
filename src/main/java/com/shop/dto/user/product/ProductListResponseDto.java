package com.shop.dto.user.product;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductListResponseDto {
    private Long productNo;
    private String name;
    private Integer price;
    private Integer discountRate;
    private String imageUrl;
    private Long categoryId;
    private String sameDayDeliveryYn;
    private LocalDateTime createdAt;
}