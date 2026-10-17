package com.example.emps.controller;

import com.example.emps.dto.report.*;
import com.example.emps.service.ReportExportService;
import com.example.emps.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportExportService reportExportService;

    public ReportController(
            ReportService reportService,
            ReportExportService reportExportService) {

        this.reportService = reportService;
        this.reportExportService = reportExportService;
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public List<EmployeeReportDto> getEmployeeReport(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(required = false) Boolean active) {

        if (search == null &&
                departmentId == null &&
                designation == null &&
                fromDate == null &&
                toDate == null &&
                active == null) {

            return reportService.getEmployeeReport();
        }

        return reportService.getEmployeeReport(
                search,
                departmentId,
                designation,
                fromDate,
                toDate,
                active
        );
    }

    @GetMapping("/employees/summary")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public EmployeeReportSummaryDto getEmployeeReportSummary() {
        return reportService.getEmployeeReportSummary();
    }

    // ==================== EMPLOYEE PDF EXPORT ====================

    @GetMapping("/employees/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<byte[]> exportEmployeesToPdf(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(required = false) Boolean active) {

        byte[] pdf =
                reportExportService.exportEmployeesToPdf(
                        search,
                        departmentId,
                        designation,
                        fromDate,
                        toDate,
                        active
                );

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_PDF
        );

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("employee-report.pdf")
                        .build()
        );

        headers.setContentLength(pdf.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    // ==================== EMPLOYEE EXCEL EXPORT ====================

    @GetMapping("/employees/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<byte[]> exportEmployeesToExcel(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(required = false) Boolean active) {

        byte[] excel =
                reportExportService.exportEmployeesToExcel(
                        search,
                        departmentId,
                        designation,
                        fromDate,
                        toDate,
                        active
                );

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("employee-report.xlsx")
                        .build()
        );

        headers.setContentLength(excel.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excel);
    }

    // ==================== EMPLOYEE CSV EXPORT ====================

    @GetMapping("/employees/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<byte[]> exportEmployeesToCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(required = false) Boolean active) {

        byte[] csv =
                reportExportService.exportEmployeesToCsv(
                        search,
                        departmentId,
                        designation,
                        fromDate,
                        toDate,
                        active
                );

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        "text/csv"
                )
        );

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("employee-report.csv")
                        .build()
        );

        headers.setContentLength(csv.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }

    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public List<DepartmentReportDto> getDepartmentReport() {
        return reportService.getDepartmentReport();
    }

    @GetMapping("/projects")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public List<ProjectReportDto> getProjectReport() {
        return reportService.getProjectReport();
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public List<TaskReportDto> getTaskReport() {
        return reportService.getTaskReport();
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public List<AttendanceReportDto> getAttendanceReport() {
        return reportService.getAttendanceReport();
    }

    @GetMapping("/leaves")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public List<LeaveReportDto> getLeaveReport() {
        return reportService.getLeaveReport();
    }

    @GetMapping("/payroll")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<PayrollReportDto> getPayrollReport() {
        return reportService.getPayrollReport();
    }

    @GetMapping("/performance")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public List<PerformanceReportDto> getPerformanceReport() {
        return reportService.getPerformanceReport();
    }

    @GetMapping("/documents")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<DocumentReportDto> getDocumentReport() {
        return reportService.getDocumentReport();
    }
}