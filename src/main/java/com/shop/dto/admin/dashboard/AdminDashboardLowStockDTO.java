package com.shop.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardLowStockDTO {

    private Long productNo;
    private Long productOptionNo;
    private String productName;
    private String optionSize;
    private String optionColor;
    private Integer stock;
    private String stockStatus;     // 상태값 (예: 위험, 주의)
}
