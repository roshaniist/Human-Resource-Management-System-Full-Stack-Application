package com.example.emps.repository;

public interface DepartmentProjection {

    Long getId();

    String getDepartmentName();

    String getDepartmentCode();

    String getDescription();

    Long getManagerId();

    String getManagerName();

    Long getEmployeeCount();
}