package com.shop.dto.seed;


import lombok.Data;

@Data
public class ProductOptionSeedRow {
    private String productKey;
    private String optionSize;
    private String color;
    private Integer stock;
    private String useYn;
}