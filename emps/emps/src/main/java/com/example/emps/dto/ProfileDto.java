package com.example.emps.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileDto {

    private Long id;

    private String employeeCode;

    private String employeeName;

    private String email;

    private String phoneNumber;

    private String designation;

    private String departmentName;

    private LocalDate joiningDate;

    private String role;

    private String status;
}