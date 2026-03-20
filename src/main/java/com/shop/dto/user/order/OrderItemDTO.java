package com.shop.dto.user.order;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private Long orderItemNo;     
    private Long orderNo;         
    private Long productOptionNo; 
    
    private int quantity;         
    private long unitPrice;   
    
    private String itemName;      
    private String itemSize;      
    private String itemColor;     
    
    private String imageUrl;      
}