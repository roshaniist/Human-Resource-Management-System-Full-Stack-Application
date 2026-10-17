package com.example.emps.dto.report;

import com.example.emps.enums.TaskStatus;

import java.time.LocalDate;

public class TaskReportDto {

    private Long taskId;
    private String taskTitle;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;

    private Long projectId;
    private String projectName;

    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    public TaskReportDto() {
    }

    public TaskReportDto(Long taskId, String taskTitle, String description,
                         LocalDate dueDate, TaskStatus status,
                         Long projectId, String projectName,
                         Long employeeId, String employeeCode, String employeeName) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.projectId = projectId;
        this.projectName = projectName;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}