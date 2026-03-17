package com.shop.dto.admin.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderDto {

    private Long orderNo;
    private String ordererName;
    private String orderStatus;
    private Long totalPrice;
    private String createdAt;
}