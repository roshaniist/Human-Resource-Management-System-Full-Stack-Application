package com.example.emps.dto;

import com.example.emps.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TaskDto {

    private Long id;

    private String taskTitle;

    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    private Long projectId;

    private List<Long> employeeIds;

    private List<TaskEmployeeDto> employees;
}