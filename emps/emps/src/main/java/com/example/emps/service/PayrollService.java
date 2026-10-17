package com.example.emps.service;

import com.example.emps.dto.PayrollDto;

import java.util.List;

public interface PayrollService {

    PayrollDto generatePayroll(PayrollDto payrollDto);

    PayrollDto updatePayroll(Long id, PayrollDto payrollDto);

    PayrollDto getPayrollById(Long id);

    List<PayrollDto> getAllPayrolls();

    List<PayrollDto> getMyPayrolls();

    List<PayrollDto> getEmployeePayrollHistory(Long employeeId);

    List<PayrollDto> getPayrollByPeriod(
            Integer payrollMonth,
            Integer payrollYear
    );

    // Payroll workflow
    void processPayroll(Long id);

    void markPayrollAsPaid(Long id);

    void cancelPayroll(Long id);

    void deletePayroll(Long id);
}