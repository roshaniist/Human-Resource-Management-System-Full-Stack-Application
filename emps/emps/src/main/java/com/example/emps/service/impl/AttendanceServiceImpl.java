package com.example.emps.service.impl;

import com.example.emps.dto.AttendanceDto;
import com.example.emps.entity.Attendance;
import com.example.emps.entity.Employee;
import com.example.emps.entity.User;
import com.example.emps.enums.AttendanceStatus;
import com.example.emps.enums.NotificationType;
import com.example.emps.repository.AttendanceRepository;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.AttendanceService;
import com.example.emps.service.NotificationService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private static final long STANDARD_WORKING_MINUTES = 480;

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // =========================================================
    // CHECK IN
    // =========================================================

    @Override
    public AttendanceDto checkIn(Long employeeId) {

        Employee employee = getEmployee(employeeId);

        checkAttendanceAccess(employee);

        LocalDate today = LocalDate.now();

        Attendance attendance =
                attendanceRepository
                        .findByEmployeeIdAndAttendanceDate(
                                employeeId,
                                today
                        )
                        .orElse(null);

        if (attendance == null) {

            LocalDateTime now = LocalDateTime.now();

            attendance = new Attendance();

            attendance.setEmployee(employee);
            attendance.setAttendanceDate(today);
            attendance.setCheckInTime(now);
            attendance.setCheckOutTime(null);

            attendance.setBreakStartTime(null);
            attendance.setBreakEndTime(null);
            attendance.setBreakDuration(0L);

            attendance.setWorkingMinutes(0L);
            attendance.setActualWorkingMinutes(0L);
            attendance.setOvertimeMinutes(0L);

            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setCreatedAt(now);
            attendance.setUpdatedAt(now);

            Attendance savedAttendance =
                    attendanceRepository.save(attendance);

            notifyEmployee(
                    savedAttendance,
                    "Attendance Checked In",
                    "Your attendance has been marked as checked in for "
                            + savedAttendance.getAttendanceDate()
                            + "."
            );

            return convertToDto(savedAttendance);
        }

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException(
                    "Attendance already completed for today"
            );
        }

        if (attendance.getBreakStartTime() != null
                && attendance.getBreakEndTime() == null) {

            throw new RuntimeException(
                    "Please end your break first"
            );
        }

        throw new RuntimeException(
                "You are already checked in"
        );
    }

    // =========================================================
    // START BREAK
    // =========================================================

    @Override
    public AttendanceDto startBreak(Long employeeId) {

        Employee employee = getEmployee(employeeId);

        checkAttendanceAccess(employee);

        Attendance attendance =
                getTodayAttendanceEntity(employeeId);

        if (attendance.getCheckInTime() == null) {
            throw new RuntimeException(
                    "Please check in first"
            );
        }

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException(
                    "Attendance already completed for today"
            );
        }

        if (attendance.getBreakStartTime() != null
                && attendance.getBreakEndTime() == null) {

            throw new RuntimeException(
                    "Break is already active"
            );
        }

        if (attendance.getBreakStartTime() != null
                && attendance.getBreakEndTime() != null) {

            throw new RuntimeException(
                    "Break has already been completed for today"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        attendance.setBreakStartTime(now);
        attendance.setUpdatedAt(now);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        notifyEmployee(
                savedAttendance,
                "Break Started",
                "Your break has been started for today's attendance."
        );

        return convertToDto(savedAttendance);
    }

    // =========================================================
    // END BREAK
    // =========================================================

    @Override
    public AttendanceDto endBreak(Long employeeId) {

        Employee employee = getEmployee(employeeId);

        checkAttendanceAccess(employee);

        Attendance attendance =
                getTodayAttendanceEntity(employeeId);

        if (attendance.getCheckInTime() == null) {
            throw new RuntimeException(
                    "Please check in first"
            );
        }

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException(
                    "Attendance already completed for today"
            );
        }

        if (attendance.getBreakStartTime() == null) {
            throw new RuntimeException(
                    "Break has not been started"
            );
        }

        if (attendance.getBreakEndTime() != null) {
            throw new RuntimeException(
                    "Break has already ended"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        long breakDuration =
                Duration.between(
                        attendance.getBreakStartTime(),
                        now
                ).toMinutes();

        attendance.setBreakEndTime(now);

        attendance.setBreakDuration(
                Math.max(0, breakDuration)
        );

        attendance.setUpdatedAt(now);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        notifyEmployee(
                savedAttendance,
                "Break Ended",
                "Your break has ended. Break duration: "
                        + savedAttendance.getBreakDuration()
                        + " minutes."
        );

        return convertToDto(savedAttendance);
    }

    // =========================================================
    // CHECK OUT
    // =========================================================

    @Override
    public AttendanceDto checkOut(Long employeeId) {

        Employee employee = getEmployee(employeeId);

        checkAttendanceAccess(employee);

        Attendance attendance =
                getTodayAttendanceEntity(employeeId);

        if (attendance.getCheckInTime() == null) {
            throw new RuntimeException(
                    "Please check in first"
            );
        }

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException(
                    "Already checked out for today"
            );
        }

        if (attendance.getBreakStartTime() != null
                && attendance.getBreakEndTime() == null) {

            throw new RuntimeException(
                    "Please end your break before checking out"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        long workingMinutes =
                Duration.between(
                        attendance.getCheckInTime(),
                        now
                ).toMinutes();

        long breakDuration =
                attendance.getBreakDuration() == null
                        ? 0L
                        : attendance.getBreakDuration();

        long actualWorkingMinutes =
                Math.max(
                        0,
                        workingMinutes - breakDuration
                );

        long overtimeMinutes =
                Math.max(
                        0,
                        actualWorkingMinutes
                                - STANDARD_WORKING_MINUTES
                );

        attendance.setCheckOutTime(now);
        attendance.setWorkingMinutes(workingMinutes);
        attendance.setBreakDuration(breakDuration);
        attendance.setActualWorkingMinutes(
                actualWorkingMinutes
        );
        attendance.setOvertimeMinutes(
                overtimeMinutes
        );
        attendance.setStatus(
                calculateStatus(actualWorkingMinutes)
        );
        attendance.setUpdatedAt(now);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        notifyEmployee(
                savedAttendance,
                "Attendance Checked Out",
                "Your attendance has been completed for "
                        + savedAttendance.getAttendanceDate()
                        + ". Actual working time: "
                        + savedAttendance.getActualWorkingMinutes()
                        + " minutes."
        );

        return convertToDto(savedAttendance);
    }

    // =========================================================
    // TODAY ATTENDANCE
    // =========================================================

    @Override
    public AttendanceDto getTodayAttendance(Long employeeId) {

        Employee employee = getEmployee(employeeId);

        checkAttendanceAccess(employee);

        LocalDate today = LocalDate.now();

        return attendanceRepository
                .findByEmployeeIdAndAttendanceDate(
                        employeeId,
                        today
                )
                .map(this::convertToDto)
                .orElse(null);
    }

    // =========================================================
    // GET ATTENDANCE BY ID
    // =========================================================

    @Override
    public AttendanceDto getAttendanceById(Long id) {

        Attendance attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Attendance record not found"
                                )
                        );

        checkAttendanceAccess(
                attendance.getEmployee()
        );

        return convertToDto(attendance);
    }

    // =========================================================
    // EMPLOYEE ATTENDANCE HISTORY
    // =========================================================

    @Override
    public List<AttendanceDto> getMyAttendance(
            Long employeeId) {

        Employee employee = getEmployee(employeeId);

        checkAttendanceAccess(employee);

        return attendanceRepository
                .findByEmployeeIdOrderByAttendanceDateDesc(
                        employeeId
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    // =========================================================
    // GET ALL ATTENDANCE
    // =========================================================

    @Override
    public List<AttendanceDto> getAllAttendance() {

        User loggedInUser = getLoggedInUser();

        String role =
                loggedInUser.getRole()
                        .getRoleName()
                        .name();

        /*
         * ADMIN and HR can see all attendance
         */
        if ("ADMIN".equals(role)
                || "HR".equals(role)) {

            return attendanceRepository
                    .findAll()
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        /*
         * MANAGER can see only
         * their department's attendance
         */
        if ("MANAGER".equals(role)) {

            Employee managerEmployee =
                    employeeRepository
                            .findByUserId(loggedInUser.getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Manager employee record not found"
                                    )
                            );

            if (managerEmployee.getDepartment() == null
                    || managerEmployee.getDepartment().getId() == null) {

                throw new RuntimeException(
                        "Manager department not found"
                );
            }

            Long departmentId =
                    managerEmployee
                            .getDepartment()
                            .getId();

            return attendanceRepository
                    .findByEmployeeDepartmentIdOrderByAttendanceDateDesc(
                            departmentId
                    )
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        throw new RuntimeException(
                "You are not authorized to access attendance"
        );
    }

    // =========================================================
    // DELETE ATTENDANCE
    // =========================================================

    @Override
    public void deleteAttendance(Long id) {

        Attendance attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Attendance record not found"
                                )
                        );

        Employee employee =
                attendance.getEmployee();

        Long attendanceId =
                attendance.getId();

        LocalDate attendanceDate =
                attendance.getAttendanceDate();

        attendanceRepository.delete(attendance);

        notifyEmployee(
                employee,
                attendanceId,
                "Attendance Deleted",
                "Your attendance record for "
                        + attendanceDate
                        + " has been deleted."
        );
    }

    // =========================================================
    // GET TODAY ATTENDANCE ENTITY
    // =========================================================

    private Attendance getTodayAttendanceEntity(
            Long employeeId) {

        LocalDate today = LocalDate.now();

        return attendanceRepository
                .findByEmployeeIdAndAttendanceDate(
                        employeeId,
                        today
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "No attendance found for today"
                        )
                );
    }

    // =========================================================
    // GET EMPLOYEE
    // =========================================================

    private Employee getEmployee(Long employeeId) {

        return employeeRepository
                .findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found"
                        )
                );
    }

    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );
    }

    // =========================================================
    // CHECK ATTENDANCE ACCESS
    // =========================================================

    private void checkAttendanceAccess(
            Employee employee) {

        if (employee == null) {
            throw new RuntimeException(
                    "Employee not found"
            );
        }

        User loggedInUser =
                getLoggedInUser();

        String role =
                loggedInUser.getRole()
                        .getRoleName()
                        .name();

        /*
         * ADMIN can access everything
         */
        if ("ADMIN".equals(role)) {
            return;
        }

        /*
         * HR can access everything
         */
        if ("HR".equals(role)) {
            return;
        }

        /*
         * EMPLOYEE can access only own attendance
         */
        if ("EMPLOYEE".equals(role)) {

            if (employee.getUser() == null
                    || employee.getUser().getId() == null
                    || !employee.getUser()
                    .getId()
                    .equals(loggedInUser.getId())) {

                throw new RuntimeException(
                        "You can access only your own attendance"
                );
            }

            return;
        }

        /*
         * MANAGER can access only
         * employees from their own department
         */
        if ("MANAGER".equals(role)) {

            Employee managerEmployee =
                    employeeRepository
                            .findByUserId(
                                    loggedInUser.getId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Manager employee record not found"
                                    )
                            );

            if (managerEmployee.getDepartment() == null
                    || managerEmployee.getDepartment().getId() == null) {

                throw new RuntimeException(
                        "Manager department not found"
                );
            }

            if (employee.getDepartment() == null
                    || employee.getDepartment().getId() == null) {

                throw new RuntimeException(
                        "Employee department not found"
                );
            }

            if (!managerEmployee
                    .getDepartment()
                    .getId()
                    .equals(
                            employee.getDepartment().getId()
                    )) {

                throw new RuntimeException(
                        "You can access attendance only for employees in your department"
                );
            }

            return;
        }

        throw new RuntimeException(
                "You are not authorized to access attendance"
        );
    }

    // =========================================================
    // ATTENDANCE STATUS
    // =========================================================

    private AttendanceStatus calculateStatus(
            long actualWorkingMinutes) {

        if (actualWorkingMinutes < 240) {
            return AttendanceStatus.HALF_DAY;
        }

        return AttendanceStatus.PRESENT;
    }

    // =========================================================
    // NOTIFICATION
    // =========================================================

    private void notifyEmployee(
            Attendance attendance,
            String title,
            String message) {

        if (attendance == null) {
            return;
        }

        notifyEmployee(
                attendance.getEmployee(),
                attendance.getId(),
                title,
                message
        );
    }

    private void notifyEmployee(
            Employee employee,
            Long attendanceId,
            String title,
            String message) {

        if (employee == null
                || employee.getUser() == null
                || employee.getUser().getId() == null) {

            return;
        }

        notificationService.createNotification(
                employee.getUser().getId(),
                title,
                message,
                NotificationType.ATTENDANCE,
                "ATTENDANCE",
                attendanceId
        );
    }

    // =========================================================
    // CONVERT TO DTO
    // =========================================================

    private AttendanceDto convertToDto(
            Attendance attendance) {

        AttendanceDto dto =
                new AttendanceDto();

        dto.setId(
                attendance.getId()
        );

        dto.setAttendanceDate(
                attendance.getAttendanceDate()
        );

        dto.setCheckInTime(
                attendance.getCheckInTime()
        );

        dto.setCheckOutTime(
                attendance.getCheckOutTime()
        );

        dto.setBreakStartTime(
                attendance.getBreakStartTime()
        );

        dto.setBreakEndTime(
                attendance.getBreakEndTime()
        );

        dto.setBreakDuration(
                attendance.getBreakDuration()
        );

        dto.setWorkingMinutes(
                attendance.getWorkingMinutes()
        );

        dto.setActualWorkingMinutes(
                attendance.getActualWorkingMinutes()
        );

        dto.setOvertimeMinutes(
                attendance.getOvertimeMinutes()
        );

        dto.setStatus(
                attendance.getStatus()
        );

        if (attendance.getEmployee() != null) {

            dto.setEmployeeId(
                    attendance
                            .getEmployee()
                            .getId()
            );
        }

        return dto;
    }
}