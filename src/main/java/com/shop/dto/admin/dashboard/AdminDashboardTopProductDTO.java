package com.shop.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardTopProductDTO {

    private Integer rank;
    private Long productNo;
    private String productName;
    private Integer totalSoldQuantity; // 판매 수량 합계
}