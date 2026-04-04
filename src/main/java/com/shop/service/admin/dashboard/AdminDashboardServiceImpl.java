package com.shop.service.admin.dashboard;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.dto.admin.dashboard.AdminDashboardLowStockDTO;
import com.shop.dto.admin.dashboard.AdminDashboardRecentOrderDTO;
import com.shop.dto.admin.dashboard.AdminDashboardResponseDTO;
import com.shop.dto.admin.dashboard.AdminDashboardSalesChartDTO;
import com.shop.dto.admin.dashboard.AdminDashboardTopProductDTO;
import com.shop.mapper.admin.AdminDashboardMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

	    private final AdminDashboardMapper adminDashboardMapper;
	
	    @Override
	    public AdminDashboardResponseDTO getDashboard() {
	
	    	Long monthGrossSales = adminDashboardMapper.getMonthGrossSales();
	    	Long monthRefundAmount = adminDashboardMapper.getMonthRefundAmount();
	
	    	if (monthGrossSales == null) {
	    	    monthGrossSales = 0L;
	    	}
	
	    	if (monthRefundAmount == null) {
	    	    monthRefundAmount = 0L;
	    	}
	
	    	

        // =========================
        // 1. 상단 요약 카드
        // =========================
	    	Long    monthSales = monthGrossSales - monthRefundAmount;
        Integer monthOrderCount = adminDashboardMapper.getMonthOrderCount();
        Integer totalMemberCount = adminDashboardMapper.getTotalMemberCount();
        Integer sellingProductCount = adminDashboardMapper.getSellingProductCount();

        // =========================
        // 2. 오늘 현황 카드
        // =========================
        Integer todayOrderCount = adminDashboardMapper.getTodayOrderCount();
        Long    todaySales = adminDashboardMapper.getTodaySales();
        Integer newMemberCount = adminDashboardMapper.getNewMemberCount();
        Integer lowStockCount = adminDashboardMapper.getLowStockCount();
        Integer refundRequestCount = adminDashboardMapper.getRefundRequestCount();

        // =========================
        // 3. 차트 / 목록 데이터
        // =========================
        List<AdminDashboardSalesChartDTO> salesChartList = adminDashboardMapper.getSalesChartList();
        List<AdminDashboardTopProductDTO> topProductList = adminDashboardMapper.getTopProductList();
        List<AdminDashboardRecentOrderDTO> recentOrderList = adminDashboardMapper.getRecentOrderList();
        List<AdminDashboardLowStockDTO> lowStockProductList = adminDashboardMapper.getLowStockProductList();
        
        Long currentWeekSales = adminDashboardMapper.getCurrentWeekSales();
        Long previousWeekSales = adminDashboardMapper.getPreviousWeekSales();
        
        // null 방어
        if (monthOrderCount == null) monthOrderCount = 0;
        if (totalMemberCount == null) totalMemberCount = 0;
        if (sellingProductCount == null) sellingProductCount = 0;

        if (todayOrderCount == null) todayOrderCount = 0;
        if (todaySales == null) todaySales = 0L;
        if (newMemberCount == null) newMemberCount = 0;
        if (lowStockCount == null) lowStockCount = 0;
        if (refundRequestCount == null) refundRequestCount = 0;
        
        if (currentWeekSales == null) currentWeekSales = 0L;
        if (previousWeekSales == null) previousWeekSales = 0L;
        
        Double weekOverWeekRate = null;
        if (previousWeekSales > 0) {
            weekOverWeekRate = ((double) (currentWeekSales - previousWeekSales) / previousWeekSales) * 100;
            weekOverWeekRate = Math.round(weekOverWeekRate * 10) / 10.0;
        }
       
        
        return AdminDashboardResponseDTO.builder()
                .monthGrossSales(monthGrossSales)
                .monthRefundAmount(monthRefundAmount)
                .monthSales(monthSales)
                .monthOrderCount(monthOrderCount)
                .totalMemberCount(totalMemberCount)
                .sellingProductCount(sellingProductCount)
                .todayOrderCount(todayOrderCount)
                .todaySales(todaySales)
                .newMemberCount(newMemberCount)
                .lowStockCount(lowStockCount)
                .refundRequestCount(refundRequestCount)
                .salesChartList(salesChartList)
                .topProductList(topProductList)
                .recentOrderList(recentOrderList)
                .lowStockProductList(lowStockProductList)
                .currentWeekSales(currentWeekSales)
                .weekOverWeekRate(weekOverWeekRate)
                .build();
    }
	    
}
