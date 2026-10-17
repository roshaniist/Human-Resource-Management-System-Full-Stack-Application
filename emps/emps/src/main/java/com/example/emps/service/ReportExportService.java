package com.example.emps.service;

import java.time.LocalDate;

public interface ReportExportService {

    byte[] exportEmployeesToPdf(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active
    );

    byte[] exportEmployeesToExcel(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active
    );

    byte[] exportEmployeesToCsv(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active
    );
}