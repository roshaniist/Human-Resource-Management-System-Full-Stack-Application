package com.example.emps.repository;

import com.example.emps.entity.Payroll;
import com.example.emps.enums.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    boolean existsByEmployeeIdAndPayrollMonthAndPayrollYear(
            Long employeeId,
            Integer payrollMonth,
            Integer payrollYear
    );

    Optional<Payroll> findByEmployeeIdAndPayrollMonthAndPayrollYear(
            Long employeeId,
            Integer payrollMonth,
            Integer payrollYear
    );

    List<Payroll>
    findByEmployeeIdOrderByPayrollYearDescPayrollMonthDesc(
            Long employeeId
    );

    List<Payroll> findByPayrollMonthAndPayrollYear(
            Integer payrollMonth,
            Integer payrollYear
    );

    List<Payroll> findByStatus(
            PayrollStatus status
    );

    List<Payroll> findByPayrollMonthAndPayrollYearAndStatus(
            Integer payrollMonth,
            Integer payrollYear,
            PayrollStatus status
    );

    List<Payroll> findByEmployeeIdAndStatus(
            Long employeeId,
            PayrollStatus status
    );

    long countByStatus(
            PayrollStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(p.netSalary), 0)
            FROM Payroll p
            WHERE p.status = :status
            """)
    BigDecimal sumNetSalaryByStatus(
            @Param("status") PayrollStatus status
    );
}