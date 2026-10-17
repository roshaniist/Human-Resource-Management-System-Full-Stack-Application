package com.example.emps.service.impl;

import com.example.emps.dto.LeaveRequestDto;
import com.example.emps.entity.Department;
import com.example.emps.entity.Employee;
import com.example.emps.entity.LeaveRequest;
import com.example.emps.entity.User;
import com.example.emps.enums.LeaveStatus;
import com.example.emps.enums.NotificationType;
import com.example.emps.repository.DepartmentRepository;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.LeaveRequestRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.LeaveRequestService;
import com.example.emps.service.NotificationService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final NotificationService notificationService;

    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            NotificationService notificationService) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.notificationService = notificationService;
    }

    // =====================================================
    // APPLY LEAVE
    // =====================================================

    @Override
    public LeaveRequestDto applyLeave(
            LeaveRequestDto leaveRequestDto) {

        if (leaveRequestDto == null) {

            throw new RuntimeException(
                    "Leave request data is required"
            );
        }

        validateDates(
                leaveRequestDto.getStartDate(),
                leaveRequestDto.getEndDate()
        );

        validateLeaveType(
                leaveRequestDto.getLeaveType()
        );

        Employee employee;

        /*
         * Employee applies leave for himself.
         */
        if (isEmployee()) {

            employee = getLoggedInEmployee();

        } else {

            /*
             * Admin / HR / Manager can apply
             * leave for an employee if required.
             */
            if (leaveRequestDto.getEmployeeId() == null) {

                throw new RuntimeException(
                        "Employee ID is required"
                );
            }

            employee = employeeRepository
                    .findById(
                            leaveRequestDto.getEmployeeId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Employee not found"
                            )
                    );
        }

        LeaveRequest leaveRequest =
                new LeaveRequest();

        leaveRequest.setStartDate(
                leaveRequestDto.getStartDate()
        );

        leaveRequest.setEndDate(
                leaveRequestDto.getEndDate()
        );

        leaveRequest.setLeaveType(
                leaveRequestDto.getLeaveType()
        );

        leaveRequest.setReason(
                leaveRequestDto.getReason()
        );

        leaveRequest.setEmployee(
                employee
        );

        leaveRequest.setStatus(
                LeaveStatus.PENDING
        );

        LeaveRequest savedLeave =
                leaveRequestRepository.save(
                        leaveRequest
                );

        notifyManagerAboutLeave(
                savedLeave,
                employee,
                "New Leave Request",
                "A new leave request has been submitted by "
                        + employee.getEmployeeName() + "."
        );

        return convertToDto(savedLeave);
    }

    // =====================================================
    // GET MY LEAVES
    // =====================================================

    @Override
    public List<LeaveRequestDto> getMyLeaves() {

        /*
         * Employee can view only his own leaves.
         */
        if (!isEmployee()) {

            throw new RuntimeException(
                    "Only employee can view own leaves"
            );
        }

        Employee employee =
                getLoggedInEmployee();

        return leaveRequestRepository
                .findByEmployeeIdOrderByStartDateDesc(
                        employee.getId()
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // =====================================================
    // GET MY LEAVE BY ID
    // =====================================================

    @Override
    public LeaveRequestDto getMyLeaveById(
            Long id) {

        if (id == null) {

            throw new RuntimeException(
                    "Leave ID is required"
            );
        }

        /*
         * Employee can access only own leave.
         */
        if (!isEmployee()) {

            throw new RuntimeException(
                    "Only employee can access own leave"
            );
        }

        Employee employee =
                getLoggedInEmployee();

        LeaveRequest leaveRequest =
                leaveRequestRepository
                        .findByIdAndEmployeeId(
                                id,
                                employee.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave request not found"
                                )
                        );

        return convertToDto(leaveRequest);
    }

    // =====================================================
    // UPDATE MY LEAVE
    // =====================================================

    @Override
    public LeaveRequestDto updateMyLeave(
            Long id,
            LeaveRequestDto leaveRequestDto) {

        if (id == null) {

            throw new RuntimeException(
                    "Leave ID is required"
            );
        }

        if (leaveRequestDto == null) {

            throw new RuntimeException(
                    "Leave request data is required"
            );
        }

        /*
         * Only employee can update own leave.
         */
        if (!isEmployee()) {

            throw new RuntimeException(
                    "Only employee can update own leave"
            );
        }

        validateDates(
                leaveRequestDto.getStartDate(),
                leaveRequestDto.getEndDate()
        );

        validateLeaveType(
                leaveRequestDto.getLeaveType()
        );

        Employee employee =
                getLoggedInEmployee();

        LeaveRequest existingLeave =
                leaveRequestRepository
                        .findByIdAndEmployeeId(
                                id,
                                employee.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave request not found"
                                )
                        );

        /*
         * Only pending leave can be updated.
         */
        if (existingLeave.getStatus()
                != LeaveStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending leave can be updated"
            );
        }

        existingLeave.setStartDate(
                leaveRequestDto.getStartDate()
        );

        existingLeave.setEndDate(
                leaveRequestDto.getEndDate()
        );

        existingLeave.setLeaveType(
                leaveRequestDto.getLeaveType()
        );

        existingLeave.setReason(
                leaveRequestDto.getReason()
        );

        LeaveRequest updatedLeave =
                leaveRequestRepository.save(
                        existingLeave
                );

        return convertToDto(updatedLeave);
    }

    // =====================================================
    // CANCEL MY LEAVE
    // =====================================================

    @Override
    public void cancelMyLeave(
            Long id) {

        if (id == null) {

            throw new RuntimeException(
                    "Leave ID is required"
            );
        }

        /*
         * Only employee can cancel own leave.
         */
        if (!isEmployee()) {

            throw new RuntimeException(
                    "Only employee can cancel own leave"
            );
        }

        Employee employee =
                getLoggedInEmployee();

        LeaveRequest leaveRequest =
                leaveRequestRepository
                        .findByIdAndEmployeeId(
                                id,
                                employee.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave request not found"
                                )
                        );

        /*
         * Only pending leave can be cancelled.
         */
        if (leaveRequest.getStatus()
                != LeaveStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending leave can be cancelled"
            );
        }

        leaveRequest.setStatus(
                LeaveStatus.CANCELLED
        );

        LeaveRequest cancelledLeave =
                leaveRequestRepository.save(
                        leaveRequest
                );

        notifyManagerAboutLeave(
                cancelledLeave,
                employee,
                "Leave Request Cancelled",
                "Leave request submitted by "
                        + employee.getEmployeeName()
                        + " has been cancelled."
        );
    }

    // =====================================================
    // GET ALL LEAVES
    // =====================================================

    @Override
    public List<LeaveRequestDto> getAllLeaves() {

        /*
         * =================================================
         * MANAGER
         * =================================================
         *
         * Manager can view only his department leaves.
         */
        if (isManager()) {

            Department department =
                    getManagerDepartment();

            return leaveRequestRepository
                    .findByEmployeeDepartmentIdOrderByStartDateDesc(
                            department.getId()
                    )
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        /*
         * =================================================
         * HR
         * =================================================
         *
         * HR can view all leaves.
         * HR cannot approve/reject.
         */
        if (isHr()) {

            return leaveRequestRepository
                    .findAll()
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        /*
         * =================================================
         * ADMIN
         * =================================================
         *
         * Admin can view all leaves.
         */
        if (isAdmin()) {

            return leaveRequestRepository
                    .findAll()
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        throw new RuntimeException(
                "You are not authorized to view leave records"
        );
    }

    // =====================================================
    // GET LEAVE BY ID
    // =====================================================

    @Override
    public LeaveRequestDto getLeaveById(
            Long id) {

        LeaveRequest leaveRequest =
                findLeaveById(id);

        /*
         * Manager:
         * Only own department.
         */
        if (isManager()) {

            checkManagerAccess(
                    leaveRequest
            );

            /*
             * Employee:
             * Only own leave.
             */
        } else if (isEmployee()) {

            checkEmployeeAccess(
                    leaveRequest.getEmployee()
            );

            /*
             * HR:
             * All leaves can be viewed.
             */
        } else if (isHr()) {

            // Allowed

            /*
             * Admin:
             * All leaves can be viewed.
             */
        } else if (isAdmin()) {

            // Allowed

        } else {

            throw new RuntimeException(
                    "You are not authorized to view this leave"
            );
        }

        return convertToDto(leaveRequest);
    }

    // =====================================================
    // APPROVE / REJECT LEAVE
    // =====================================================

    @Override
    public LeaveRequestDto updateLeaveStatus(
            Long id,
            LeaveStatus status,
            String rejectionReason) {

        if (id == null) {

            throw new RuntimeException(
                    "Leave ID is required"
            );
        }

        if (status == null) {

            throw new RuntimeException(
                    "Leave status is required"
            );
        }

        /*
         * =================================================
         * ONLY MANAGER AND ADMIN
         * =================================================
         *
         * HR cannot approve/reject.
         * Employee cannot approve/reject.
         */
        if (!isManager() && !isAdmin()) {

            throw new RuntimeException(
                    "Only department manager or admin can approve or reject leave"
            );
        }

        LeaveRequest leaveRequest =
                findLeaveById(id);

        /*
         * =================================================
         * MANAGER DEPARTMENT ACCESS
         * =================================================
         *
         * Manager can approve/reject only leaves
         * from his own department.
         */
        if (isManager()) {

            checkManagerAccess(
                    leaveRequest
            );
        }

        /*
         * =================================================
         * CANCELLED CHECK
         * =================================================
         */

        if (leaveRequest.getStatus()
                == LeaveStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled leave cannot be approved or rejected"
            );
        }

        /*
         * =================================================
         * PENDING CHECK
         * =================================================
         */

        if (leaveRequest.getStatus()
                != LeaveStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending leave can change status"
            );
        }

        /*
         * =================================================
         * STATUS CHECK
         * =================================================
         */

        if (status != LeaveStatus.APPROVED &&
                status != LeaveStatus.REJECTED) {

            throw new RuntimeException(
                    "Leave can only be approved or rejected"
            );
        }

        /*
         * =================================================
         * REJECT
         * =================================================
         */

        if (status == LeaveStatus.REJECTED) {

            /*
             * Rejection reason is mandatory.
             */
            if (rejectionReason == null ||
                    rejectionReason.trim().isEmpty()) {

                throw new RuntimeException(
                        "Rejection reason is required"
                );
            }

            /*
             * Get logged-in Manager/Admin.
             */
            User loggedInUser =
                    getLoggedInUser();

            /*
             * Set status.
             */
            leaveRequest.setStatus(
                    LeaveStatus.REJECTED
            );

            /*
             * Save rejection reason.
             */
            leaveRequest.setRejectionReason(
                    rejectionReason.trim()
            );

            /*
             * Save Manager/Admin who rejected.
             */
            leaveRequest.setRejectedBy(
                    loggedInUser
            );

            /*
             * Save rejection date/time.
             */
            leaveRequest.setRejectedAt(
                    LocalDateTime.now()
            );
        }

        /*
         * =================================================
         * APPROVE
         * =================================================
         */

        else {

            leaveRequest.setStatus(
                    LeaveStatus.APPROVED
            );

            /*
             * Approved leave should not contain
             * rejection information.
             */
            leaveRequest.setRejectionReason(
                    null
            );

            leaveRequest.setRejectedBy(
                    null
            );

            leaveRequest.setRejectedAt(
                    null
            );
        }

        LeaveRequest updatedLeave =
                leaveRequestRepository.save(
                        leaveRequest
                );

        /*
         * Notify employee.
         */
        notifyEmployeeAboutStatus(
                updatedLeave,
                status
        );

        return convertToDto(
                updatedLeave
        );
    }

    // =====================================================
    // DELETE LEAVE
    // =====================================================

    @Override
    public void deleteLeave(
            Long id) {

        /*
         * Only ADMIN can delete leave.
         */
        if (!isAdmin()) {

            throw new RuntimeException(
                    "Only ADMIN can delete leave"
            );
        }

        LeaveRequest leaveRequest =
                findLeaveById(id);

        leaveRequestRepository.delete(
                leaveRequest
        );
    }

    // =====================================================
    // FIND LEAVE BY ID
    // =====================================================

    private LeaveRequest findLeaveById(
            Long id) {

        if (id == null) {

            throw new RuntimeException(
                    "Leave ID is required"
            );
        }

        return leaveRequestRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Leave request not found"
                        )
                );
    }

    // =====================================================
    // GET LOGGED-IN USER
    // =====================================================

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email =
                authentication.getName();

        if (email == null ||
                email.isBlank()) {

            throw new RuntimeException(
                    "Logged-in user email not found"
            );
        }

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );
    }

    // =====================================================
    // GET LOGGED-IN EMPLOYEE
    // =====================================================

    private Employee getLoggedInEmployee() {

        User loggedInUser =
                getLoggedInUser();

        return employeeRepository
                .findByUserId(
                        loggedInUser.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee profile not found"
                        )
                );
    }

    // =====================================================
    // GET MANAGER DEPARTMENT
    // =====================================================

    private Department getManagerDepartment() {

        User manager =
                getLoggedInUser();

        return departmentRepository
                .findByManagerId(
                        manager.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found for logged-in manager"
                        )
                );
    }

    // =====================================================
    // CHECK EMPLOYEE ACCESS
    // =====================================================

    private void checkEmployeeAccess(
            Employee employee) {

        Employee loggedInEmployee =
                getLoggedInEmployee();

        if (employee == null ||
                employee.getId() == null ||
                !employee.getId().equals(
                        loggedInEmployee.getId()
                )) {

            throw new RuntimeException(
                    "You can access only your own leave"
            );
        }
    }

    // =====================================================
    // CHECK MANAGER ACCESS
    // =====================================================

    private void checkManagerAccess(
            LeaveRequest leaveRequest) {

        if (leaveRequest == null) {

            throw new RuntimeException(
                    "Leave request not found"
            );
        }

        Employee employee =
                leaveRequest.getEmployee();

        if (employee == null) {

            throw new RuntimeException(
                    "Employee not found for this leave"
            );
        }

        if (employee.getDepartment() == null) {

            throw new RuntimeException(
                    "Employee department not found"
            );
        }

        Department managerDepartment =
                getManagerDepartment();

        if (managerDepartment.getId() == null ||
                employee.getDepartment().getId() == null ||
                !employee.getDepartment()
                        .getId()
                        .equals(
                                managerDepartment.getId()
                        )) {

            throw new RuntimeException(
                    "You can access leave requests only from your department"
            );
        }
    }

    // =====================================================
    // ROLE CHECKS
    // =====================================================

    private boolean isEmployee() {

        return hasRole("ROLE_EMPLOYEE");
    }

    private boolean isManager() {

        return hasRole("ROLE_MANAGER");
    }

    private boolean isHr() {

        return hasRole("ROLE_HR");
    }

    private boolean isAdmin() {

        return hasRole("ROLE_ADMIN");
    }

    private boolean hasRole(
            String role) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {

            return false;
        }

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals(role)
                );
    }

    // =====================================================
    // VALIDATE DATES
    // =====================================================

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null ||
                endDate == null) {

            throw new RuntimeException(
                    "Start date and end date are required"
            );
        }

        if (endDate.isBefore(startDate)) {

            throw new RuntimeException(
                    "End date cannot be before start date"
            );
        }
    }

    // =====================================================
    // VALIDATE LEAVE TYPE
    // =====================================================

    private void validateLeaveType(
            com.example.emps.enums.LeaveType leaveType) {

        if (leaveType == null) {

            throw new RuntimeException(
                    "Leave type is required"
            );
        }
    }

    // =====================================================
    // NOTIFY MANAGER
    // =====================================================

    private void notifyManagerAboutLeave(
            LeaveRequest leaveRequest,
            Employee employee,
            String title,
            String message) {

        if (employee == null ||
                employee.getDepartment() == null) {

            return;
        }

        Department department =
                employee.getDepartment();

        User manager =
                department.getManager();

        if (manager == null ||
                manager.getId() == null) {

            return;
        }

        notificationService.createNotification(
                manager.getId(),
                title,
                message,
                NotificationType.LEAVE,
                "LEAVE_REQUEST",
                leaveRequest.getId()
        );
    }

    // =====================================================
    // NOTIFY EMPLOYEE ABOUT STATUS
    // =====================================================

    private void notifyEmployeeAboutStatus(
            LeaveRequest leaveRequest,
            LeaveStatus status) {

        if (leaveRequest == null ||
                leaveRequest.getEmployee() == null ||
                leaveRequest.getEmployee().getUser() == null) {

            return;
        }

        User employeeUser =
                leaveRequest
                        .getEmployee()
                        .getUser();

        if (employeeUser.getId() == null) {

            return;
        }

        String title;
        String message;

        if (status == LeaveStatus.APPROVED) {

            title = "Leave Request Approved";

            message =
                    "Your leave request from "
                            + leaveRequest.getStartDate()
                            + " to "
                            + leaveRequest.getEndDate()
                            + " has been approved.";

        } else {

            title = "Leave Request Rejected";

            message =
                    "Your leave request from "
                            + leaveRequest.getStartDate()
                            + " to "
                            + leaveRequest.getEndDate()
                            + " has been rejected."
                            + " Reason: "
                            + leaveRequest.getRejectionReason();
        }

        notificationService.createNotification(
                employeeUser.getId(),
                title,
                message,
                NotificationType.LEAVE,
                "LEAVE_REQUEST",
                leaveRequest.getId()
        );
    }

    // =====================================================
    // CONVERT ENTITY TO DTO
    // =====================================================

    private LeaveRequestDto convertToDto(
            LeaveRequest leaveRequest) {

        LeaveRequestDto dto =
                new LeaveRequestDto();

        dto.setId(
                leaveRequest.getId()
        );

        dto.setStartDate(
                leaveRequest.getStartDate()
        );

        dto.setEndDate(
                leaveRequest.getEndDate()
        );

        dto.setLeaveType(
                leaveRequest.getLeaveType()
        );

        dto.setStatus(
                leaveRequest.getStatus()
        );

        dto.setReason(
                leaveRequest.getReason()
        );

        // =================================================
        // EMPLOYEE ID
        // =================================================

        if (leaveRequest.getEmployee() != null) {

            dto.setEmployeeId(
                    leaveRequest
                            .getEmployee()
                            .getId()
            );
        }

        // =================================================
        // REJECTION INFORMATION
        // =================================================

        dto.setRejectionReason(
                leaveRequest.getRejectionReason()
        );

        dto.setRejectedAt(
                leaveRequest.getRejectedAt()
        );

        /*
         * Only rejectedByName is returned.
         *
         * rejectedById is NOT returned in DTO.
         */
        if (leaveRequest.getRejectedBy() != null) {

            String firstName =
                    leaveRequest
                            .getRejectedBy()
                            .getFirstName();

            String lastName =
                    leaveRequest
                            .getRejectedBy()
                            .getLastName();

            dto.setRejectedByName(
                    firstName + " " + lastName
            );
        }

        return dto;
    }
}
