package com.example.emps.controller;

import com.example.emps.dto.PayrollDto;
import com.example.emps.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PostMapping
    public ResponseEntity<PayrollDto> generatePayroll(
            @Valid @RequestBody PayrollDto payrollDto) {

        PayrollDto createdPayroll =
                payrollService.generatePayroll(payrollDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdPayroll);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PutMapping("/{id}")
    public ResponseEntity<PayrollDto> updatePayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollDto payrollDto) {

        PayrollDto updatedPayroll =
                payrollService.updatePayroll(
                        id,
                        payrollDto
                );

        return ResponseEntity.ok(updatedPayroll);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PatchMapping("/{id}/process")
    public ResponseEntity<Void> processPayroll(
            @PathVariable Long id) {

        payrollService.processPayroll(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PatchMapping("/{id}/pay")
    public ResponseEntity<Void> markPayrollAsPaid(
            @PathVariable Long id) {

        payrollService.markPayrollAsPaid(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPayroll(
            @PathVariable Long id) {

        payrollService.cancelPayroll(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<PayrollDto> getPayrollById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.getPayrollById(id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping
    public ResponseEntity<List<PayrollDto>> getAllPayrolls() {

        return ResponseEntity.ok(
                payrollService.getAllPayrolls()
        );
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<PayrollDto>> getMyPayrolls() {

        return ResponseEntity.ok(
                payrollService.getMyPayrolls()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayrollDto>> getEmployeePayrollHistory(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                payrollService.getEmployeePayrollHistory(
                        employeeId
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping("/period/{month}/{year}")
    public ResponseEntity<List<PayrollDto>> getPayrollByPeriod(
            @PathVariable Integer month,
            @PathVariable Integer year) {

        return ResponseEntity.ok(
                payrollService.getPayrollByPeriod(
                        month,
                        year
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(
            @PathVariable Long id) {

        payrollService.deletePayroll(id);

        return ResponseEntity.noContent().build();
    }
}