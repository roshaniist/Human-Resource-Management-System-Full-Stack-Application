package com.example.emps.controller;

import com.example.emps.dto.EmployeeDto;
import com.example.emps.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // =========================================================
    // MY EMPLOYEE
    // EMPLOYEE / MANAGER
    // =========================================================

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    @GetMapping("/me")
    public ResponseEntity<EmployeeDto> getMyEmployee() {

        return ResponseEntity.ok(
                employeeService.getMyEmployee()
        );
    }


    // =========================================================
    // GET EMPLOYEE BY ID
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeService.getEmployeeById(id)
        );
    }


    // =========================================================
    // MY TEAM
    // MANAGER
    // =========================================================

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/my-team")
    public ResponseEntity<List<EmployeeDto>> getMyTeam() {

        return ResponseEntity.ok(
                employeeService.getMyTeam()
        );
    }


    // =========================================================
    // GET ALL EMPLOYEES
    // ADMIN / HR / IT_SUPPORT
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'IT_SUPPORT')")
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {

        return ResponseEntity.ok(
                employeeService.getAllEmployees()
        );
    }


    // =========================================================
    // GET ALL MANAGERS
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/managers")
    public ResponseEntity<List<EmployeeDto>> getAllManagers() {

        return ResponseEntity.ok(
                employeeService.getAllManagers()
        );
    }


    // =========================================================
    // CREATE EMPLOYEE
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(
            @Valid @RequestBody EmployeeDto employeeDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        employeeService.createEmployee(employeeDto)
                );
    }


    // =========================================================
    // UPDATE EMPLOYEE
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDto employeeDto) {

        return ResponseEntity.ok(
                employeeService.updateEmployee(
                        id,
                        employeeDto
                )
        );
    }


    // =========================================================
    // DELETE EMPLOYEE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }
}