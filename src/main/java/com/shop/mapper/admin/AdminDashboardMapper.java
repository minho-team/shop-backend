package com.shop.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.shop.dto.admin.dashboard.AdminDashboardLowStockDTO;
import com.shop.dto.admin.dashboard.AdminDashboardRecentOrderDTO;
import com.shop.dto.admin.dashboard.AdminDashboardSalesChartDTO;
import com.shop.dto.admin.dashboard.AdminDashboardTopProductDTO;

@Mapper
public interface AdminDashboardMapper {
	
	// =========================
    // 1. 상단 요약 카드
    // =========================
	
	Long    getMonthGrossSales();
    Long    getMonthRefundAmount();
    Integer getMonthOrderCount();
    Integer getTotalMemberCount();
    Integer getSellingProductCount();

	// =========================
    // 2. 오늘 현황 카드
    // =========================
	
    Integer getTodayOrderCount();
    Long    getTodaySales();
    Integer getNewMemberCount();
    Integer getLowStockCount();
    Integer getRefundRequestCount();

	
	// =========================
    // 3. 차트 / 목록 데이터
    // =========================
	
    List<AdminDashboardSalesChartDTO> getSalesChartList();
    List<AdminDashboardTopProductDTO> getTopProductList();
    List<AdminDashboardRecentOrderDTO> getRecentOrderList();
    List<AdminDashboardLowStockDTO> getLowStockProductList();
	
    Long getCurrentWeekSales();
    Long getPreviousWeekSales();
	
}
