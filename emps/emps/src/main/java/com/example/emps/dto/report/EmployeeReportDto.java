package com.example.emps.dto.report;

import java.time.LocalDate;
import java.util.List;

public class EmployeeReportDto {

    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String email;
    private String phoneNumber;
    private String departmentName;
    private String designation;

    private List<String> projects;
    private List<String> assignedTasks;

    private Double salary;
    private LocalDate joiningDate;
    private boolean active;

    public EmployeeReportDto() {
    }

    public EmployeeReportDto(
            Long employeeId,
            String employeeCode,
            String employeeName,
            String email,
            String phoneNumber,
            String departmentName,
            String designation,
            List<String> projects,
            List<String> assignedTasks,
            Double salary,
            LocalDate joiningDate,
            boolean active) {

        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.departmentName = departmentName;
        this.designation = designation;
        this.projects = projects;
        this.assignedTasks = assignedTasks;
        this.salary = salary;
        this.joiningDate = joiningDate;
        this.active = active;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public List<String> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(List<String> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}