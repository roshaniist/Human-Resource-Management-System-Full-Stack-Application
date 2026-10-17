package com.example.emps.dto;

import com.example.emps.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceDto {

    private Long id;

    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Long breakDuration;

    private LocalDateTime breakStartTime;

    private LocalDateTime breakEndTime;

    private Long workingMinutes;

    private Long actualWorkingMinutes;

    private Long overtimeMinutes;

    private AttendanceStatus status;

    private Long employeeId;

    public AttendanceDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
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
}