package com.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private Long orderItemNo;   
    private Long orderNo;        
    private Long productOptionNo; 
    
    private Integer quantity;         
    private Long unitPrice;
    // 주문상품에 배분된 쿠폰 할인금액
    private Long couponDiscountAmount;
    
    private String itemName;      
    private String itemSize;     
    private String itemColor;     
    
    private String orderItemStatus;
    private Integer refundedQuantity;
    private String imageUrl;
}
