package com.example.emps.dto;

import com.example.emps.enums.RoleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeDto {

    private Long id;

    private String employeeCode;

    private String employeeName;

    private String gender;

    private String phoneNumber;

    private String designation;

    private Double salary;

    private LocalDate joiningDate;

    private Long userId;

    private Long departmentId;

    private RoleType role;
}