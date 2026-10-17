package com.example.emps.dto.report;

import com.example.emps.enums.PerformanceReviewStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PerformanceReportDto {

    private Long performanceReviewId;
    private LocalDate reviewDate;
    private BigDecimal rating;
    private String reviewPeriod;
    private String comments;
    private PerformanceReviewStatus status;

    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    private Long reviewerId;
    private String reviewerName;

    private boolean employeeAcknowledged;
    private LocalDateTime acknowledgedAt;

    public PerformanceReportDto() {
    }

    public PerformanceReportDto(Long performanceReviewId, LocalDate reviewDate,
                                BigDecimal rating, String reviewPeriod,
                                String comments, PerformanceReviewStatus status,
                                Long employeeId, String employeeCode,
                                String employeeName, Long reviewerId,
                                String reviewerName, boolean employeeAcknowledged,
                                LocalDateTime acknowledgedAt) {
        this.performanceReviewId = performanceReviewId;
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.reviewPeriod = reviewPeriod;
        this.comments = comments;
        this.status = status;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.employeeAcknowledged = employeeAcknowledged;
        this.acknowledgedAt = acknowledgedAt;
    }

    public Long getPerformanceReviewId() {
        return performanceReviewId;
    }

    public void setPerformanceReviewId(Long performanceReviewId) {
        this.performanceReviewId = performanceReviewId;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public PerformanceReviewStatus getStatus() {
        return status;
    }

    public void setStatus(PerformanceReviewStatus status) {
        this.status = status;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public boolean isEmployeeAcknowledged() {
        return employeeAcknowledged;
    }

    public void setEmployeeAcknowledged(boolean employeeAcknowledged) {
        this.employeeAcknowledged = employeeAcknowledged;
    }

    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }
}