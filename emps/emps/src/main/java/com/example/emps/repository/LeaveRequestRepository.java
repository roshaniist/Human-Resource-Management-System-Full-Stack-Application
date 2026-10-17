package com.example.emps.repository;

import com.example.emps.entity.LeaveRequest;
import com.example.emps.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeIdOrderByStartDateDesc(
            Long employeeId
    );

    List<LeaveRequest> findByEmployeeIdAndStatusOrderByStartDateDesc(
            Long employeeId,
            LeaveStatus status
    );

    Optional<LeaveRequest> findByIdAndEmployeeId(
            Long id,
            Long employeeId
    );

    List<LeaveRequest> findByEmployeeDepartmentIdOrderByStartDateDesc(
            Long departmentId
    );

    List<LeaveRequest>
    findByEmployeeDepartmentIdAndStatusOrderByStartDateDesc(
            Long departmentId,
            LeaveStatus status
    );

    long countByStatus(LeaveStatus status);
}