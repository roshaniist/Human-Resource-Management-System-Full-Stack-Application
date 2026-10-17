package com.example.emps.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectEmployeeDto {

    private Long id;

    private String employeeCode;

    private String phoneNumber;

    private String employeeName;

    private String email;

    private String designation;

    private String departmentName;

    public ProjectEmployeeDto() {
    }

    public ProjectEmployeeDto(
            Long id,
            String employeeCode,
            String phoneNumber,
            String employeeName,
            String email,
            String designation,
            String departmentName) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.phoneNumber = phoneNumber;
        this.employeeName = employeeName;
        this.email = email;
        this.designation = designation;
        this.departmentName = departmentName;
    }
}