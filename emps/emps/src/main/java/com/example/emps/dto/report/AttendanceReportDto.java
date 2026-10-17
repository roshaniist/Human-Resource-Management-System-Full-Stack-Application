package com.example.emps.dto.report;

import com.example.emps.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceReportDto {

    private Long attendanceId;
    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private LocalDateTime breakStartTime;
    private LocalDateTime breakEndTime;

    private Long breakDuration;
    private Long workingMinutes;
    private Long actualWorkingMinutes;
    private Long overtimeMinutes;

    private AttendanceStatus status;

    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    public AttendanceReportDto() {
    }

    public AttendanceReportDto(Long attendanceId, LocalDate attendanceDate,
                               LocalDateTime checkInTime, LocalDateTime checkOutTime,
                               LocalDateTime breakStartTime, LocalDateTime breakEndTime,
                               Long breakDuration, Long workingMinutes,
                               Long actualWorkingMinutes, Long overtimeMinutes,
                               AttendanceStatus status, Long employeeId,
                               String employeeCode, String employeeName) {
        this.attendanceId = attendanceId;
        this.attendanceDate = attendanceDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.breakDuration = breakDuration;
        this.workingMinutes = workingMinutes;
        this.actualWorkingMinutes = actualWorkingMinutes;
        this.overtimeMinutes = overtimeMinutes;
        this.status = status;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public LocalDateTime getBreakStartTime() {
        return breakStartTime;
    }

    public void setBreakStartTime(LocalDateTime breakStartTime) {
        this.breakStartTime = breakStartTime;
    }

    public LocalDateTime getBreakEndTime() {
        return breakEndTime;
    }

    public void setBreakEndTime(LocalDateTime breakEndTime) {
        this.breakEndTime = breakEndTime;
    }

    public Long getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(Long breakDuration) {
        this.breakDuration = breakDuration;
    }

    public Long getWorkingMinutes() {
        return workingMinutes;
    }

    public void setWorkingMinutes(Long workingMinutes) {
        this.workingMinutes = workingMinutes;
    }

    public Long getActualWorkingMinutes() {
        return actualWorkingMinutes;
    }

    public void setActualWorkingMinutes(Long actualWorkingMinutes) {
        this.actualWorkingMinutes = actualWorkingMinutes;
    }

    public Long getOvertimeMinutes() {
        return overtimeMinutes;
    }

    public void setOvertimeMinutes(Long overtimeMinutes) {
        this.overtimeMinutes = overtimeMinutes;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
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