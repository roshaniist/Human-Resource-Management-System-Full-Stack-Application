package com.example.emps.service;

import com.example.emps.dto.DashboardDto;

public interface DashboardService {

    DashboardDto getDashboardData(String email);
}