package com.shop.dto.user.order;

import lombok.Data;

@Data
public class OrderSearchDTO {
    private Integer page;
    private Integer size;
    private Long orderNo;       
    private String productName;
    private String orderStatus; 
}