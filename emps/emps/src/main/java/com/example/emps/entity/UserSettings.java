package com.example.emps.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(nullable = false)
    private boolean emailNotifications = true;

    @Column(nullable = false)
    private boolean leaveUpdates = true;

    @Column(nullable = false)
    private boolean taskUpdates = true;

    @Column(nullable = false)
    private boolean projectUpdates = true;

    @Column(nullable = false)
    private String theme = "SYSTEM";

    @Column(nullable = false)
    private String language = "English";

    @Column(nullable = false)
    private String dateFormat = "DD/MM/YYYY";

    public UserSettings() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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