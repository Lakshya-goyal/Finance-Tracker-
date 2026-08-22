package com.financetracker.controller;

import com.financetracker.dto.DashboardResponse;
import com.financetracker.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DashboardResponse> getDashboardData(@PathVariable Long userId) {
        DashboardResponse dashboardData = dashboardService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }
}
