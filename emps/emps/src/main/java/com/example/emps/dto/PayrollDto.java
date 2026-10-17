package com.example.emps.dto;

import com.example.emps.enums.PayrollStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PayrollDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be positive")
    private Long employeeId;

    @NotNull(message = "Payroll month is required")
    @Min(
            value = 1,
            message = "Payroll month must be between 1 and 12"
    )
    @Max(
            value = 12,
            message = "Payroll month must be between 1 and 12"
    )
    private Integer payrollMonth;

    @NotNull(message = "Payroll year is required")
    @Min(
            value = 2000,
            message = "Invalid payroll year"
    )
    @Max(
            value = 2100,
            message = "Invalid payroll year"
    )
    private Integer payrollYear;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Basic salary cannot be negative"
    )
    private BigDecimal basicSalary;

    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Bonus cannot be negative"
    )
    private BigDecimal bonus;

    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Allowances cannot be negative"
    )
    private BigDecimal allowances;

    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Deductions cannot be negative"
    )
    private BigDecimal deductions;

    private BigDecimal grossSalary;

    private BigDecimal netSalary;

    private LocalDate paymentDate;
    private PayrollStatus status;
}