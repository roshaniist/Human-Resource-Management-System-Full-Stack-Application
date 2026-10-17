package com.example.emps.entity;

import com.example.emps.enums.PayrollStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "payroll",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_payroll_period",
                        columnNames = {
                                "employee_id",
                                "payroll_month",
                                "payroll_year"
                        }
                )
        }
)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false
    )
    private Employee employee;

    @Column(
            name = "payroll_month",
            nullable = false
    )
    private Integer payrollMonth;

    @Column(
            name = "payroll_year",
            nullable = false
    )
    private Integer payrollYear;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal basicSalary;

    @Column(
            precision = 12,
            scale = 2
    )
    private BigDecimal bonus;

    @Column(
            precision = 12,
            scale = 2
    )
    private BigDecimal allowances;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal grossSalary;

    @Column(
            precision = 12,
            scale = 2
    )
    private BigDecimal deductions;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal netSalary;

    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status;

    public Payroll() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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
}