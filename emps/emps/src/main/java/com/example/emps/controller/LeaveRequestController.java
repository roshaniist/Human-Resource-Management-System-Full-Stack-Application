package com.example.emps.controller;

import com.example.emps.dto.LeaveRequestDto;
import com.example.emps.enums.LeaveStatus;
import com.example.emps.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(
            LeaveRequestService leaveRequestService) {

        this.leaveRequestService = leaveRequestService;
    }

    // =========================================================
    // EMPLOYEE - MY LEAVES
    // =========================================================

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<LeaveRequestDto>> getMyLeaves() {

        return ResponseEntity.ok(
                leaveRequestService.getMyLeaves()
        );
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my/{id}")
    public ResponseEntity<LeaveRequestDto> getMyLeaveById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                leaveRequestService.getMyLeaveById(id)
        );
    }

    // =========================================================
    // EMPLOYEE - APPLY LEAVE
    // =========================================================

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<LeaveRequestDto> applyLeave(
            @Valid @RequestBody LeaveRequestDto leaveRequestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        leaveRequestService.applyLeave(
                                leaveRequestDto
                        )
                );
    }

    // =========================================================
    // EMPLOYEE - UPDATE LEAVE
    // =========================================================

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/my/{id}")
    public ResponseEntity<LeaveRequestDto> updateMyLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveRequestDto leaveRequestDto) {

        return ResponseEntity.ok(
                leaveRequestService.updateMyLeave(
                        id,
                        leaveRequestDto
                )
        );
    }

    // =========================================================
    // EMPLOYEE - CANCEL LEAVE
    // =========================================================

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/my/{id}/cancel")
    public ResponseEntity<Void> cancelMyLeave(
            @PathVariable Long id) {

        leaveRequestService.cancelMyLeave(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // ADMIN / HR / MANAGER - VIEW LEAVES
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<LeaveRequestDto>> getAllLeaves() {

        return ResponseEntity.ok(
                leaveRequestService.getAllLeaves()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDto> getLeaveById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                leaveRequestService.getLeaveById(id)
        );
    }

    // =========================================================
    // ADMIN / MANAGER - APPROVE / REJECT LEAVE
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}/status")
    public ResponseEntity<LeaveRequestDto> updateLeaveStatus(
            @PathVariable Long id,
            @RequestParam LeaveStatus status,
            @RequestParam(required = false) String rejectionReason) {

        return ResponseEntity.ok(
                leaveRequestService.updateLeaveStatus(
                        id,
                        status,
                        rejectionReason
                )
        );
    }

    // =========================================================
    // ADMIN - DELETE LEAVE
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(
            @PathVariable Long id) {

        leaveRequestService.deleteLeave(id);

        return ResponseEntity.noContent().build();
    }
}