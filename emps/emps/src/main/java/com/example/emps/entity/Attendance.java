package com.example.emps.entity;

import com.example.emps.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_attendance_date",
                        columnNames = {
                                "employee_id",
                                "attendance_date"
                        }
                )
        }
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "attendance_date",
            nullable = false
    )
    private LocalDate attendanceDate;


    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "break_start_time")
    private LocalDateTime breakStartTime;



    @Column(name = "break_end_time")
    private LocalDateTime breakEndTime;


    @Column(name = "break_duration")
    private Long breakDuration;


    @Column(name = "working_minutes")
    private Long workingMinutes;

    @Column(name = "actual_working_minutes")
    private Long actualWorkingMinutes;

    @Column(name = "overtime_minutes")
    private Long overtimeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false
    )
    private AttendanceStatus status;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "employee_id",
            nullable = false
    )
    private Employee employee;

    @Version
    private Long version;


    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}