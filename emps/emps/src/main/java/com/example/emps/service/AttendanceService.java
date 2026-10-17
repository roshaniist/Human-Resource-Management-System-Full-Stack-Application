package com.example.emps.service;

import com.example.emps.dto.AttendanceDto;

import java.util.List;

public interface AttendanceService {

    AttendanceDto checkIn(Long employeeId);

    AttendanceDto startBreak(Long employeeId);

    AttendanceDto endBreak(Long employeeId);

    AttendanceDto checkOut(Long employeeId);

    AttendanceDto getTodayAttendance(Long employeeId);

    AttendanceDto getAttendanceById(Long id);

    List<AttendanceDto> getMyAttendance(Long employeeId);

    List<AttendanceDto> getAllAttendance();

    void deleteAttendance(Long id);
}