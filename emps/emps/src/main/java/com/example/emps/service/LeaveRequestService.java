package com.example.emps.service;

import com.example.emps.dto.LeaveRequestDto;
import com.example.emps.enums.LeaveStatus;

import java.util.List;

public interface LeaveRequestService {

    LeaveRequestDto applyLeave(
            LeaveRequestDto leaveRequestDto
    );

    List<LeaveRequestDto> getMyLeaves();

    LeaveRequestDto getMyLeaveById(
            Long id
    );

    LeaveRequestDto updateMyLeave(
            Long id,
            LeaveRequestDto leaveRequestDto
    );

    void cancelMyLeave(
            Long id
    );

    List<LeaveRequestDto> getAllLeaves();

    LeaveRequestDto getLeaveById(
            Long id
    );

    /*
     * ADMIN / Department Manager can approve or reject.
     *
     * rejectionReason is required only when
     * status is REJECTED.
     */
    LeaveRequestDto updateLeaveStatus(
            Long id,
            LeaveStatus status,
            String rejectionReason
    );

    void deleteLeave(
            Long id
    );
}