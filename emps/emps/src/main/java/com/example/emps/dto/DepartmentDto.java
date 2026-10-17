package com.example.emps.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentDto {

    private Long id;

    private String departmentName;

    private String departmentCode;

    private String description;

    private Long managerId;

    private String managerName;

    private Long employeeCount;
}