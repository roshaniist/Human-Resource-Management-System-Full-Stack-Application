package com.example.emps.entity;

import com.example.emps.enums.PerformanceReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "performance_reviews",
        indexes = {

                @Index(
                        name = "idx_performance_employee",
                        columnList = "employee_id"
                ),

                @Index(
                        name = "idx_performance_reviewer",
                        columnList = "reviewer_id"
                ),

                @Index(
                        name = "idx_performance_review_date",
                        columnList = "review_date"
                ),

                @Index(
                        name = "idx_performance_status",
                        columnList = "status"
                ),

                @Index(
                        name = "idx_performance_employee_status",
                        columnList = "employee_id, status"
                ),

                @Index(
                        name = "idx_performance_status_review_date",
                        columnList = "status, review_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PerformanceReview {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // REVIEW DETAILS
    // =========================================================

    @NotNull
    @Column(
            name = "review_date",
            nullable = false
    )
    private LocalDate reviewDate;


    @NotNull
    @DecimalMin(
            value = "0.00",
            message = "Rating cannot be less than 0"
    )
    @DecimalMax(
            value = "5.00",
            message = "Rating cannot be greater than 5"
    )
    @Column(
            nullable = false,
            precision = 3,
            scale = 2
    )
    private BigDecimal rating;


    @NotBlank
    @Column(
            name = "review_period",
            nullable = false,
            length = 50
    )
    private String reviewPeriod;


    @Column(
            columnDefinition = "TEXT"
    )
    private String comments;


    // =========================================================
    // WORKFLOW STATUS
    // =========================================================

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private PerformanceReviewStatus status =
            PerformanceReviewStatus.DRAFT;


    // =========================================================
    // EMPLOYEE
    // =========================================================

    @NotNull
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_performance_employee"
            )
    )
    private Employee employee;


    // =========================================================
    // REVIEWER
    // =========================================================

    @NotNull
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "reviewer_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_performance_reviewer"
            )
    )
    private User reviewer;


    // =========================================================
    // EMPLOYEE ACKNOWLEDGEMENT
    // =========================================================

    @Column(
            name = "employee_acknowledged",
            nullable = false
    )
    private boolean employeeAcknowledged = false;


    @Column(
            name = "acknowledged_at"
    )
    private LocalDateTime acknowledgedAt;


    // =========================================================
    // AUDIT FIELDS
    // =========================================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at"
    )
    private LocalDateTime updatedAt;


    // =========================================================
    // JPA LIFECYCLE
    // =========================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}