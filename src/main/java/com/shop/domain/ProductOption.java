package com.shop.domain;

import lombok.Data;

@Data
public class ProductOption {
	private Long productOptionNo;
    private Long productNo;
    private String optionSize;
    private String color;
    private Integer stock;
    private String useYn;
}
