package com.shop.dto.user.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderListRequest {

    private int page = 1;
    private int size = 10;

    // 검색 조건 (orderNo | productName)
    private String searchType; 
    private String keyword;

    // 기간 필터 (recentWeek | custom)
    private String datePreset; 
    private String startDate;    // yyyy-MM-dd
    private String endDate;      // yyyy-MM-dd

    // 주문 상태 [cite: 30, 36]
    private String orderStatus;

    public int getOffset() {
        return (page - 1) * size;
    }

    public String getSearchType() {
        return searchType == null ? "" : searchType.trim();
    }

    public String getKeyword() {
        return keyword == null ? "" : keyword.trim();
    }

    public String getDatePreset() {
        return datePreset == null ? "" : datePreset.trim();
    }

    public String getStartDate() {
        return startDate == null ? "" : startDate.trim();
    }

    public String getEndDate() {
        return endDate == null ? "" : endDate.trim();
    }

    public String getOrderStatus() {
        return orderStatus == null ? "" : orderStatus.trim();
    }
}