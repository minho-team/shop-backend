package com.shop.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.admin.dashboard.AdminDashboardResponseDTO;
import com.shop.service.admin.dashboard.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

	private final AdminDashboardService adminDashboardService;

    // 관리자 대시보드 조회
    @GetMapping
    public ResponseEntity<AdminDashboardResponseDTO> getDashboard() {
        AdminDashboardResponseDTO responseDTO = adminDashboardService.getDashboard();
        return ResponseEntity.ok(responseDTO);
    }
}
