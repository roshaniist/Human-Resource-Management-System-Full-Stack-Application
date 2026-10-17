package com.example.emps.dto;

import com.example.emps.enums.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ProjectDto {

    private Long id;

    private String projectName;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private ProjectStatus status;

    private Long managerId;

    private ProjectEmployeeDto manager;

    private List<Long> employeeIds;

    private List<ProjectEmployeeDto> employees;
}