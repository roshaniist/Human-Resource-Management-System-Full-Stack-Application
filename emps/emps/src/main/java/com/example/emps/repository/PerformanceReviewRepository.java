package com.example.emps.repository;

import com.example.emps.entity.PerformanceReview;
import com.example.emps.enums.PerformanceReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceReviewRepository
        extends JpaRepository<PerformanceReview, Long> {

    // =========================================================
    // EMPLOYEE REVIEWS
    // =========================================================

    List<PerformanceReview> findByEmployeeId(Long employeeId);

    List<PerformanceReview> findByEmployeeIdOrderByReviewDateDesc(
            Long employeeId
    );

    List<PerformanceReview> findByEmployeeIdAndStatus(
            Long employeeId,
            PerformanceReviewStatus status
    );

    List<PerformanceReview> findByEmployeeIdAndStatusOrderByReviewDateDesc(
            Long employeeId,
            PerformanceReviewStatus status
    );


    // =========================================================
    // REVIEWER REVIEWS
    // =========================================================

    List<PerformanceReview> findByReviewerId(Long reviewerId);

    List<PerformanceReview> findByReviewerIdOrderByReviewDateDesc(
            Long reviewerId
    );

    List<PerformanceReview> findByReviewerIdAndStatus(
            Long reviewerId,
            PerformanceReviewStatus status
    );

    List<PerformanceReview> findByReviewerIdAndStatusOrderByReviewDateDesc(
            Long reviewerId,
            PerformanceReviewStatus status
    );


    // =========================================================
    // DUPLICATE REVIEW CHECK
    // =========================================================

    boolean existsByEmployeeIdAndReviewPeriod(
            Long employeeId,
            String reviewPeriod
    );


    // =========================================================
    // STATUS SUMMARY
    // =========================================================

    long countByStatus(
            PerformanceReviewStatus status
    );
}