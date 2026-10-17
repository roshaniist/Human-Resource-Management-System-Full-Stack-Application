package com.example.emps.dto;

import com.example.emps.enums.LeaveStatus;
import com.example.emps.enums.LeaveType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LeaveRequestDto {

    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private LeaveType leaveType;

    private LeaveStatus status;

    private String reason;

    private Long employeeId;

    /*
     * =====================================================
     * REJECTION INFORMATION
     * =====================================================
     */

    /**
     * Reason provided by Manager/Admin
     * when rejecting the leave request.
     */
    private String rejectionReason;

    /**
     * Name of the Manager/Admin who rejected the leave.
     *
     * Example:
     * Saleem Ahmed
     */
    private String rejectedByName;

    /**
     * Date and time when the leave was rejected.
     */
    private LocalDateTime rejectedAt;
}