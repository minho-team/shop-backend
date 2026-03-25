package com.shop.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSalesChartDTO {

    private String label;   // 예: 1주차, 2주차, 3주차, 4주차
    private Long sales;     // 해당 구간 매출
}
