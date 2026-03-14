package com.shop.dto.user.product;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProductCreateRequest {
    private String name;

    private Integer price;

    private Integer salePrice; // 할인가 (없을 수 있음)

    private Integer categoryId;

    private String description;

    private String sameDayDeliveryYn; // 당일 배송 여부
}
