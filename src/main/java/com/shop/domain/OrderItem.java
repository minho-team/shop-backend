package com.shop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItem {
    private Long orderItemNo;
    private Long orderNo;        
    private Long productOptionNo;
    private Integer productNo;   
    private Integer quantity;
    private Integer unitPrice;
    private String itemName;
    private String itemSize;
    private String itemColor;
    private String imageUrl;
}