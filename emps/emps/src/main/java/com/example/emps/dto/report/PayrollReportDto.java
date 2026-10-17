package com.example.emps.dto.report;

import com.example.emps.enums.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollReportDto {

    private Long payrollId;

    private Integer payrollMonth;
    private Integer payrollYear;

    private BigDecimal basicSalary;
    private BigDecimal bonus;
    private BigDecimal allowances;
    private BigDecimal grossSalary;
    private BigDecimal deductions;
    private BigDecimal netSalary;

    private LocalDate paymentDate;
    private PayrollStatus status;

    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    public PayrollReportDto() {
    }

    public PayrollReportDto(Long payrollId, Integer payrollMonth, Integer payrollYear,
                            BigDecimal basicSalary, BigDecimal bonus,
                            BigDecimal allowances, BigDecimal grossSalary,
                            BigDecimal deductions, BigDecimal netSalary,
                            LocalDate paymentDate, PayrollStatus status,
                            Long employeeId, String employeeCode,
                            String employeeName) {
        this.payrollId = payrollId;
        this.payrollMonth = payrollMonth;
        this.payrollYear = payrollYear;
        this.basicSalary = basicSalary;
        this.bonus = bonus;
        this.allowances = allowances;
        this.grossSalary = grossSalary;
        this.deductions = deductions;
        this.netSalary = netSalary;
        this.paymentDate = paymentDate;
        this.status = status;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
    }

    public Long getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(Long payrollId) {
        this.payrollId = payrollId;
    }

    public Integer getPayrollMonth() {
        return payrollMonth;
    }

    public void setPayrollMonth(Integer payrollMonth) {
        this.payrollMonth = payrollMonth;
    }

    public Integer getPayrollYear() {
        return payrollYear;
    }

    public void setPayrollYear(Integer payrollYear) {
        this.payrollYear = payrollYear;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public void setAllowances(BigDecimal allowances) {
        this.allowances = allowances;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PayrollStatus getStatus() {
        return status;
    }

    public void setStatus(PayrollStatus status) {
        this.status = status;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}