package com.shop.dto.admin.dashboard;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardRecentOrderDTO {

    private Long orderNo;
    private String ordererName;
    private String orderStatus;
    private Long totalPrice;
    private LocalDateTime createdAt;
}