package com.example.emps.dto;

public class UserSettingsDto {

    private Long id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private boolean accountStatus;

    private boolean emailNotifications;

    private boolean leaveUpdates;

    private boolean taskUpdates;

    private boolean projectUpdates;

    private String theme;

    private String language;

    private String dateFormat;

    public UserSettingsDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(boolean accountStatus) {
        this.accountStatus = accountStatus;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isLeaveUpdates() {
        return leaveUpdates;
    }

    public void setLeaveUpdates(boolean leaveUpdates) {
        this.leaveUpdates = leaveUpdates;
    }

    public boolean isTaskUpdates() {
        return taskUpdates;
    }

    public void setTaskUpdates(boolean taskUpdates) {
        this.taskUpdates = taskUpdates;
    }

    public boolean isProjectUpdates() {
        return projectUpdates;
    }

    public void setProjectUpdates(boolean projectUpdates) {
        this.projectUpdates = projectUpdates;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}