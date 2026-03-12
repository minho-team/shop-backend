package com.shop.dto.seed;


import lombok.Data;

@Data
public class ProductSeedRow {
    private String productKey;
    private String name;
    private Long price;
    private Long salePrice;
    private Long categoryId;
    private String description;
    private String useYn;
    private String sameDayDeliveryYn;
}
