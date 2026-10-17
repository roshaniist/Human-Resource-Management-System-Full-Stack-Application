package com.example.emps.dto.report;

public class EmployeeReportSummaryDto {

    private Long totalEmployees;
    private Long activeEmployees;
    private Long inactiveEmployees;
    private Double averageSalary;

    public EmployeeReportSummaryDto() {
    }

    public EmployeeReportSummaryDto(
            Long totalEmployees,
            Long activeEmployees,
            Long inactiveEmployees,
            Double averageSalary) {

        this.totalEmployees = totalEmployees;
        this.activeEmployees = activeEmployees;
        this.inactiveEmployees = inactiveEmployees;
        this.averageSalary = averageSalary;
    }

    public Long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(Long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public Long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(Long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public Long getInactiveEmployees() {
        return inactiveEmployees;
    }

    public void setInactiveEmployees(Long inactiveEmployees) {
        this.inactiveEmployees = inactiveEmployees;
    }

    public Double getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(Double averageSalary) {
        this.averageSalary = averageSalary;
    }
}