package com.example.emps.service;

import com.example.emps.dto.report.AttendanceReportDto;
import com.example.emps.dto.report.DepartmentReportDto;
import com.example.emps.dto.report.DocumentReportDto;
import com.example.emps.dto.report.EmployeeReportDto;
import com.example.emps.dto.report.EmployeeReportSummaryDto;
import com.example.emps.dto.report.LeaveReportDto;
import com.example.emps.dto.report.PayrollReportDto;
import com.example.emps.dto.report.PerformanceReportDto;
import com.example.emps.dto.report.ProjectReportDto;
import com.example.emps.dto.report.TaskReportDto;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<EmployeeReportDto> getEmployeeReport();

    List<EmployeeReportDto> getEmployeeReport(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active
    );

    EmployeeReportSummaryDto getEmployeeReportSummary();

    List<DepartmentReportDto> getDepartmentReport();

    List<ProjectReportDto> getProjectReport();

    List<TaskReportDto> getTaskReport();

    List<AttendanceReportDto> getAttendanceReport();

    List<LeaveReportDto> getLeaveReport();

    List<PayrollReportDto> getPayrollReport();

    List<PerformanceReportDto> getPerformanceReport();

    List<DocumentReportDto> getDocumentReport();
}