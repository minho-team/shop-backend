package com.shop.dto.admin.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 대시보드 전체 응답 최상위 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponseDTO {

    // =========================
    // 1. 상단 요약 카드
    // =========================
    private Long monthSales;              // 금월 최종매출 (총매출-총환불금액 = 최종매출)
    private Long monthGrossSales; 		 // 금월 총매출
    private Long monthRefundAmount;      // 금월 총환불금액
    
    private Integer monthOrderCount;      // 금월 주문 수
    private Integer totalMemberCount;     // 전체 회원 수
    private Integer sellingProductCount;  // 판매중 상품 수
    
    // =========================
    // 2. 오늘 현황 카드
    // =========================
    private Integer todayOrderCount;      // 오늘 주문 수
    private Long todaySales;              // 오늘 매출
    private Integer newMemberCount;       // 신규 회원 수
    private Integer lowStockCount;        // 재고 부족 상품 수
    private Integer refundRequestCount;   // 환불 요청 건수

    // =========================
    // 3. 차트 / 목록 데이터
    // =========================
    private List<AdminDashboardSalesChartDTO> salesChartList;
    private List<AdminDashboardTopProductDTO> topProductList;
    private List<AdminDashboardRecentOrderDTO> recentOrderList;
    private List<AdminDashboardLowStockDTO> lowStockProductList;
}