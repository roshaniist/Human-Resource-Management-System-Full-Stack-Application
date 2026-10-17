package com.example.emps.dto;

import com.example.emps.enums.PerformanceReviewStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PerformanceReviewDto {

    // =========================================================
    // IDENTIFIER
    // =========================================================

    private Long id;


    // =========================================================
    // REVIEW DETAILS
    // =========================================================

    @NotNull(message = "Review date is required")
    private LocalDate reviewDate;


    @NotNull(message = "Rating is required")
    @DecimalMin(
            value = "0.00",
            message = "Rating cannot be less than 0"
    )
    @DecimalMax(
            value = "5.00",
            message = "Rating cannot be greater than 5"
    )
    private BigDecimal rating;


    @NotBlank(message = "Review period is required")
    @Size(
            max = 50,
            message = "Review period cannot exceed 50 characters"
    )
    private String reviewPeriod;


    @Size(
            max = 2000,
            message = "Comments cannot exceed 2000 characters"
    )
    private String comments;


    // =========================================================
    // WORKFLOW STATUS
    // =========================================================

    private PerformanceReviewStatus status;


    // =========================================================
    // EMPLOYEE / REVIEWER
    // =========================================================

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be positive")
    private Long employeeId;


    /*
     * Kept for backward compatibility with the current API.
     *
     * Reviewer assignment/authorization will be controlled
     * by the backend service and will not be blindly trusted
     * from the frontend.
     */
    @Positive(message = "Reviewer ID must be positive")
    private Long reviewerId;


    // =========================================================
    // ACKNOWLEDGEMENT
    // =========================================================

    private boolean employeeAcknowledged;

    private LocalDateTime acknowledgedAt;


    // =========================================================
    // AUDIT INFORMATION
    // =========================================================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}