package com.example.emps.controller;

import com.example.emps.dto.PerformanceReviewDto;
import com.example.emps.enums.PerformanceReviewStatus;
import com.example.emps.service.PerformanceReviewService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance-reviews")
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    public PerformanceReviewController(
            PerformanceReviewService performanceReviewService) {

        this.performanceReviewService =
                performanceReviewService;
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping
    public ResponseEntity<PerformanceReviewDto> createReview(
            @Valid @RequestBody PerformanceReviewDto reviewDto) {

        PerformanceReviewDto createdReview =
                performanceReviewService.createReview(
                        reviewDto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReview);
    }

    // =========================================================
    // GET ALL REVIEWS
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<PerformanceReviewDto>> getAllReviews() {

        return ResponseEntity.ok(
                performanceReviewService.getAllReviews()
        );
    }

    // =========================================================
    // GET MY REVIEWS
    // =========================================================

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-reviews")
    public ResponseEntity<List<PerformanceReviewDto>> getMyReviews() {

        return ResponseEntity.ok(
                performanceReviewService.getMyReviews()
        );
    }

    // =========================================================
    // GET REVIEW BY ID
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')"
    )
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PerformanceReviewDto> getReviewById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                performanceReviewService.getReviewById(id)
        );
    }

    // =========================================================
    // GET REVIEWS BY EMPLOYEE
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER')"
    )
    @GetMapping("/employee/{employeeId:\\d+}")
    public ResponseEntity<List<PerformanceReviewDto>> getReviewsByEmployee(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                performanceReviewService
                        .getReviewsByEmployee(employeeId)
        );
    }

    // =========================================================
    // GET REVIEWS BY EMPLOYEE + STATUS
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER')"
    )
    @GetMapping("/employee/{employeeId:\\d+}/status/{status}")
    public ResponseEntity<List<PerformanceReviewDto>>
    getReviewsByEmployeeAndStatus(
            @PathVariable Long employeeId,
            @PathVariable PerformanceReviewStatus status) {

        return ResponseEntity.ok(
                performanceReviewService
                        .getReviewsByEmployeeAndStatus(
                                employeeId,
                                status
                        )
        );
    }

    // =========================================================
    // GET REVIEWS BY REVIEWER
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER')"
    )
    @GetMapping("/reviewer/{reviewerId:\\d+}")
    public ResponseEntity<List<PerformanceReviewDto>> getReviewsByReviewer(
            @PathVariable Long reviewerId) {

        return ResponseEntity.ok(
                performanceReviewService
                        .getReviewsByReviewer(reviewerId)
        );
    }

    // =========================================================
    // GET REVIEWS BY REVIEWER + STATUS
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER')"
    )
    @GetMapping("/reviewer/{reviewerId:\\d+}/status/{status}")
    public ResponseEntity<List<PerformanceReviewDto>>
    getReviewsByReviewerAndStatus(
            @PathVariable Long reviewerId,
            @PathVariable PerformanceReviewStatus status) {

        return ResponseEntity.ok(
                performanceReviewService
                        .getReviewsByReviewerAndStatus(
                                reviewerId,
                                status
                        )
        );
    }

    // =========================================================
    // UPDATE REVIEW
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER')"
    )
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<PerformanceReviewDto> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceReviewDto reviewDto) {

        return ResponseEntity.ok(
                performanceReviewService.updateReview(
                        id,
                        reviewDto
                )
        );
    }

    // =========================================================
    // SUBMIT REVIEW
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER')"
    )
    @PatchMapping("/{id:\\d+}/submit")
    public ResponseEntity<PerformanceReviewDto> submitReview(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                performanceReviewService.submitReview(id)
        );
    }

    // =========================================================
    // ACKNOWLEDGE REVIEW
    // =========================================================

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/{id:\\d+}/acknowledge")
    public ResponseEntity<Void> acknowledgeReview(
            @PathVariable Long id) {

        performanceReviewService.acknowledgeReview(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // =========================================================
    // COMPLETE REVIEW
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER')"
    )
    @PatchMapping("/{id:\\d+}/complete")
    public ResponseEntity<PerformanceReviewDto> completeReview(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                performanceReviewService.completeReview(id)
        );
    }

    // =========================================================
    // CANCEL REVIEW
    // =========================================================

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'HR', 'MANAGER')"
    )
    @PatchMapping("/{id:\\d+}/cancel")
    public ResponseEntity<PerformanceReviewDto> cancelReview(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                performanceReviewService.cancelReview(id)
        );
    }

    // =========================================================
    // DELETE REVIEW
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id) {

        performanceReviewService.deleteReview(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}