package com.shop.dto.user.order;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemDTO {
    private int orderItemNo;
    private long orderNo;
    private Integer productNo;  
    private Long productOptionNo; 
    private int quantity;
    private int unitPrice;
    private String itemName;
    private String itemSize;
    private String itemColor;
    private String imageUrl;
}