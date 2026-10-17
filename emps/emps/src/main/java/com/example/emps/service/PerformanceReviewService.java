package com.example.emps.service;

import com.example.emps.dto.PerformanceReviewDto;
import com.example.emps.enums.PerformanceReviewStatus;

import java.util.List;

public interface PerformanceReviewService {

    // =========================================================
    // CREATE
    // =========================================================

    PerformanceReviewDto createReview(
            PerformanceReviewDto reviewDto
    );


    // =========================================================
    // READ
    // =========================================================

    List<PerformanceReviewDto> getAllReviews();

    List<PerformanceReviewDto> getMyReviews();

    PerformanceReviewDto getReviewById(
            Long id
    );

    List<PerformanceReviewDto> getReviewsByEmployee(
            Long employeeId
    );

    List<PerformanceReviewDto> getReviewsByEmployeeAndStatus(
            Long employeeId,
            PerformanceReviewStatus status
    );

    List<PerformanceReviewDto> getReviewsByReviewer(
            Long reviewerId
    );

    List<PerformanceReviewDto> getReviewsByReviewerAndStatus(
            Long reviewerId,
            PerformanceReviewStatus status
    );


    // =========================================================
    // UPDATE
    // =========================================================

    PerformanceReviewDto updateReview(
            Long id,
            PerformanceReviewDto reviewDto
    );


    // =========================================================
    // WORKFLOW
    // =========================================================

    PerformanceReviewDto submitReview(
            Long id
    );

    void acknowledgeReview(
            Long id
    );

    PerformanceReviewDto completeReview(
            Long id
    );

    PerformanceReviewDto cancelReview(
            Long id
    );


    // =========================================================
    // DELETE
    // =========================================================

    void deleteReview(
            Long id
    );
}