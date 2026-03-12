package com.shop.dto.seed;

import lombok.Data;

@Data
public class ProductImageSeedRow {
    private String productKey;
    private String imageFileName;
    private String imageType;
    private Integer sortOrder;
}