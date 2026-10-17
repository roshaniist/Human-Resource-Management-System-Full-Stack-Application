package com.example.emps.controller;

import com.example.emps.dto.DashboardDto;
import com.example.emps.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard(
            Authentication authentication) {

        String email = authentication.getName();

        DashboardDto dashboard =
                dashboardService.getDashboardData(email);

        return ResponseEntity.ok(dashboard);
    }
}