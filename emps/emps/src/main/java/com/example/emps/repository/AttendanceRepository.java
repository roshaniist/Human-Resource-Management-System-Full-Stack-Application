package com.example.emps.repository;

import com.example.emps.entity.Attendance;
import com.example.emps.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(
            Long employeeId,
            LocalDate attendanceDate
    );

    List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(
            Long employeeId
    );

    List<Attendance>
    findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Attendance> findByAttendanceDate(
            LocalDate attendanceDate
    );

    /*
     * Manager department-wise attendance
     */
    List<Attendance> findByEmployeeDepartmentIdOrderByAttendanceDateDesc(
            Long departmentId
    );

    boolean existsByEmployeeIdAndAttendanceDate(
            Long employeeId,
            LocalDate attendanceDate
    );

    long countByAttendanceDateAndStatus(
            LocalDate attendanceDate,
            AttendanceStatus status
    );
}