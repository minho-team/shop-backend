package com.shop.dto.user.payment;

import lombok.Data;

@Data
public class PaymentPrepareItemDTO {
    private Long productOptionNo;
    private Integer quantity;
    private Long unitPrice;

    private String itemName;
    private String itemSize;
    private String itemColor;
    private String imageUrl;
}
