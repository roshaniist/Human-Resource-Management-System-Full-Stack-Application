package com.example.emps.controller;

import com.example.emps.dto.AttendanceDto;
import com.example.emps.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(
            AttendanceService attendanceService) {

        this.attendanceService = attendanceService;
    }


    // =====================================================
    // CHECK IN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<AttendanceDto> checkIn(
            @PathVariable Long employeeId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        attendanceService.checkIn(employeeId)
                );
    }


    // =====================================================
    // START BREAK
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @PostMapping("/break-start/{employeeId}")
    public ResponseEntity<AttendanceDto> startBreak(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                attendanceService.startBreak(employeeId)
        );
    }


    // =====================================================
    // END BREAK / RESUME WORK
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @PostMapping("/break-end/{employeeId}")
    public ResponseEntity<AttendanceDto> endBreak(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                attendanceService.endBreak(employeeId)
        );
    }


    // =====================================================
    // FINAL CHECK OUT
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @PostMapping("/check-out/{employeeId}")
    public ResponseEntity<AttendanceDto> checkOut(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                attendanceService.checkOut(employeeId)
        );
    }


    // =====================================================
    // TODAY ATTENDANCE
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/today/{employeeId}")
    public ResponseEntity<AttendanceDto> getTodayAttendance(
            @PathVariable Long employeeId) {

        AttendanceDto attendance =
                attendanceService.getTodayAttendance(employeeId);

        if (attendance == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(attendance);
    }


    // =====================================================
    // GET ATTENDANCE BY ID
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDto> getAttendanceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceById(id)
        );
    }


    // =====================================================
    // EMPLOYEE ATTENDANCE HISTORY
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceDto>> getMyAttendance(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                attendanceService.getMyAttendance(employeeId)
        );
    }


    // =====================================================
    // ALL ATTENDANCE
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<AttendanceDto>> getAllAttendance() {

        return ResponseEntity.ok(
                attendanceService.getAllAttendance()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Long id) {

        attendanceService.deleteAttendance(id);

        return ResponseEntity.noContent().build();
    }
}