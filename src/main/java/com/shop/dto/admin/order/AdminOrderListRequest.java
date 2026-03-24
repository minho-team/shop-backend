package com.shop.dto.admin.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderListRequest {

	 private int page = 1;
	    private int size = 10;

	    // 검색 조건
	    private String searchType;   // orderNo | ordererName
	    private String keyword;

	    // 날짜 필터
	    private String datePreset;   // recentWeek | custom
	    private String startDate;    // yyyy-MM-dd
	    private String endDate;      // yyyy-MM-dd

	    // 주문 상태
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