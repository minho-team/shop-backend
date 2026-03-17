package com.shop.dto.user.order;

import java.util.Date; // java.util 패키지 확인
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    private int orderNo;
    private int memberNo;
    private String orderStatus;
    private long totalPrice;
    private Date createdAt; // Timestamp 대신 Date 사용
}