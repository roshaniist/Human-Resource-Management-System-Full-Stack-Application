package com.example.emps.dto;

import java.math.BigDecimal;

public class DashboardDto {

    private long totalEmployees;
    private long totalDepartments;

    private long totalProjects;
    private long activeProjects;

    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long overdueTasks;

    private double completedTaskPercentage;
    private double pendingTaskPercentage;
    private double overdueTaskPercentage;

    private long todayPresent;
    private long todayLate;
    private long todayHalfDay;
    private long todayAbsent;
    private long todayLeave;

    private long pendingLeaveRequests;

    private long draftPayrolls;
    private long processedPayrolls;
    private long paidPayrolls;
    private long cancelledPayrolls;
    private BigDecimal totalPaidSalary;

    private long draftReviews;
    private long submittedReviews;
    private long acknowledgedReviews;
    private long completedReviews;
    private long cancelledReviews;

    private long unreadNotifications;

    private long assignedTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;

    public DashboardDto() {
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(long totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(long activeProjects) {
        this.activeProjects = activeProjects;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public long getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }

    public double getCompletedTaskPercentage() {
        return completedTaskPercentage;
    }

    public void setCompletedTaskPercentage(double completedTaskPercentage) {
        this.completedTaskPercentage = completedTaskPercentage;
    }

    public double getPendingTaskPercentage() {
        return pendingTaskPercentage;
    }

    public void setPendingTaskPercentage(double pendingTaskPercentage) {
        this.pendingTaskPercentage = pendingTaskPercentage;
    }

    public double getOverdueTaskPercentage() {
        return overdueTaskPercentage;
    }

    public void setOverdueTaskPercentage(double overdueTaskPercentage) {
        this.overdueTaskPercentage = overdueTaskPercentage;
    }

    public long getTodayPresent() {
        return todayPresent;
    }

    public void setTodayPresent(long todayPresent) {
        this.todayPresent = todayPresent;
    }

    public long getTodayLate() {
        return todayLate;
    }

    public void setTodayLate(long todayLate) {
        this.todayLate = todayLate;
    }

    public long getTodayHalfDay() {
        return todayHalfDay;
    }

    public void setTodayHalfDay(long todayHalfDay) {
        this.todayHalfDay = todayHalfDay;
    }

    public long getTodayAbsent() {
        return todayAbsent;
    }

    public void setTodayAbsent(long todayAbsent) {
        this.todayAbsent = todayAbsent;
    }

    public long getTodayLeave() {
        return todayLeave;
    }

    public void setTodayLeave(long todayLeave) {
        this.todayLeave = todayLeave;
    }

    public long getPendingLeaveRequests() {
        return pendingLeaveRequests;
    }

    public void setPendingLeaveRequests(long pendingLeaveRequests) {
        this.pendingLeaveRequests = pendingLeaveRequests;
    }

    public long getDraftPayrolls() {
        return draftPayrolls;
    }

    public void setDraftPayrolls(long draftPayrolls) {
        this.draftPayrolls = draftPayrolls;
    }

    public long getProcessedPayrolls() {
        return processedPayrolls;
    }

    public void setProcessedPayrolls(long processedPayrolls) {
        this.processedPayrolls = processedPayrolls;
    }

    public long getPaidPayrolls() {
        return paidPayrolls;
    }

    public void setPaidPayrolls(long paidPayrolls) {
        this.paidPayrolls = paidPayrolls;
    }

    public long getCancelledPayrolls() {
        return cancelledPayrolls;
    }

    public void setCancelledPayrolls(long cancelledPayrolls) {
        this.cancelledPayrolls = cancelledPayrolls;
    }

    public BigDecimal getTotalPaidSalary() {
        return totalPaidSalary;
    }

    public void setTotalPaidSalary(BigDecimal totalPaidSalary) {
        this.totalPaidSalary = totalPaidSalary;
    }

    public long getDraftReviews() {
        return draftReviews;
    }

    public void setDraftReviews(long draftReviews) {
        this.draftReviews = draftReviews;
    }

    public long getSubmittedReviews() {
        return submittedReviews;
    }

    public void setSubmittedReviews(long submittedReviews) {
        this.submittedReviews = submittedReviews;
    }

    public long getAcknowledgedReviews() {
        return acknowledgedReviews;
    }

    public void setAcknowledgedReviews(long acknowledgedReviews) {
        this.acknowledgedReviews = acknowledgedReviews;
    }

    public long getCompletedReviews() {
        return completedReviews;
    }

    public void setCompletedReviews(long completedReviews) {
        this.completedReviews = completedReviews;
    }

    public long getCancelledReviews() {
        return cancelledReviews;
    }

    public void setCancelledReviews(long cancelledReviews) {
        this.cancelledReviews = cancelledReviews;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(long unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public long getAssignedTickets() {
        return assignedTickets;
    }

    public void setAssignedTickets(long assignedTickets) {
        this.assignedTickets = assignedTickets;
    }

    public long getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }

    public long getInProgressTickets() {
        return inProgressTickets;
    }

    public void setInProgressTickets(long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }

    public long getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }
}