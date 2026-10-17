package com.example.emps.service;

import com.example.emps.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(Long id);

    EmployeeDto getMyEmployee();

    List<EmployeeDto> getAllEmployees();

    List<EmployeeDto> getAllManagers();

    List<EmployeeDto> getMyTeam();

    void deleteEmployee(Long id);
}