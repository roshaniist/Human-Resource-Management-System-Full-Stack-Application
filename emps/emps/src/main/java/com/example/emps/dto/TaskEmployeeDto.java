package com.example.emps.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskEmployeeDto {

    private Long id;
    private String employeeName;
    private String email;
    private String designation;

    public TaskEmployeeDto() {
    }

    public TaskEmployeeDto(
            Long id,
            String employeeName,
            String email,
            String designation) {
        this.id = id;
        this.employeeName = employeeName;
        this.email = email;
        this.designation = designation;
    }
}