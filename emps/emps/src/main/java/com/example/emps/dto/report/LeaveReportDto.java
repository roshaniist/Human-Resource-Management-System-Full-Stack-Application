package com.example.emps.dto.report;

import com.example.emps.enums.LeaveStatus;
import com.example.emps.enums.LeaveType;

import java.time.LocalDate;

public class LeaveReportDto {

    private Long leaveId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    private LeaveStatus status;
    private String reason;
    private Long totalLeaveDays;

    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    public LeaveReportDto() {
    }

    public LeaveReportDto(Long leaveId, LocalDate startDate, LocalDate endDate,
                          LeaveType leaveType, LeaveStatus status, String reason,
                          Long totalLeaveDays, Long employeeId,
                          String employeeCode, String employeeName) {
        this.leaveId = leaveId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.status = status;
        this.reason = reason;
        this.totalLeaveDays = totalLeaveDays;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
    }

    public Long getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Long leaveId) {
        this.leaveId = leaveId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getTotalLeaveDays() {
        return totalLeaveDays;
    }

    public void setTotalLeaveDays(Long totalLeaveDays) {
        this.totalLeaveDays = totalLeaveDays;
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