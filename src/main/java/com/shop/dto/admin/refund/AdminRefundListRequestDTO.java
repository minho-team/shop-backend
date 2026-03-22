package com.shop.dto.admin.refund;

import lombok.Data;

@Data
public class AdminRefundListRequestDTO {
    private int page = 1;
    private int size = 10;
    private String keyword = "";
    private String status = "ALL";

    public int getOffset() {
        return (page - 1) * size;
    }
}
