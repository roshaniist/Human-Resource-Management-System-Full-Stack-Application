package com.example.emps.service.impl;

import com.example.emps.dto.ChangePasswordRequest;
import com.example.emps.dto.UserSettingsDto;
import com.example.emps.entity.Employee;
import com.example.emps.entity.User;
import com.example.emps.entity.UserSettings;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.repository.UserSettingsRepository;
import com.example.emps.service.AuthService;
import com.example.emps.service.SettingsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingsServiceImpl implements SettingsService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final AuthService authService;

    public SettingsServiceImpl(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            UserSettingsRepository userSettingsRepository,
            AuthService authService) {

        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.authService = authService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserSettingsDto getMySettings() {

        User user = getLoggedInUser();

        UserSettings settings = userSettingsRepository
                .findByUserId(user.getId())
                .orElseGet(() -> createDefaultSettingsObject());

        Employee employee = employeeRepository
                .findByUserId(user.getId())
                .orElse(null);

        return convertToDto(user, employee, settings);
    }

    @Override
    public UserSettingsDto updateMySettings(
            UserSettingsDto settingsDto) {

        User user = getLoggedInUser();

        UserSettings settings = userSettingsRepository
                .findByUserId(user.getId())
                .orElseGet(() -> createDefaultSettings(user));

        Employee employee = employeeRepository
                .findByUserId(user.getId())
                .orElse(null);

        if (settingsDto.getFullName() != null
                && !settingsDto.getFullName().trim().isEmpty()) {

            String fullName = settingsDto.getFullName().trim();

            String[] nameParts = fullName.split("\\s+", 2);

            user.setFirstName(nameParts[0]);

            if (nameParts.length > 1) {
                user.setLastName(nameParts[1]);
            } else {
                user.setLastName("");
            }
        }

        if (employee != null
                && settingsDto.getPhoneNumber() != null) {

            employee.setPhoneNumber(
                    settingsDto.getPhoneNumber().trim()
            );

            employeeRepository.save(employee);
        }

        settings.setEmailNotifications(
                settingsDto.isEmailNotifications()
        );

        settings.setLeaveUpdates(
                settingsDto.isLeaveUpdates()
        );

        settings.setTaskUpdates(
                settingsDto.isTaskUpdates()
        );

        settings.setProjectUpdates(
                settingsDto.isProjectUpdates()
        );

        if (settingsDto.getTheme() != null
                && !settingsDto.getTheme().trim().isEmpty()) {

            settings.setTheme(
                    settingsDto.getTheme().trim().toUpperCase()
            );
        }

        if (settingsDto.getLanguage() != null
                && !settingsDto.getLanguage().trim().isEmpty()) {

            settings.setLanguage(
                    settingsDto.getLanguage().trim()
            );
        }

        if (settingsDto.getDateFormat() != null
                && !settingsDto.getDateFormat().trim().isEmpty()) {

            settings.setDateFormat(
                    settingsDto.getDateFormat().trim()
            );
        }

        userRepository.save(user);

        userSettingsRepository.save(settings);

        return convertToDto(
                user,
                employee,
                settings
        );
    }

    @Override
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        authService.changePassword(
                email,
                request
        );
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        ));
    }

    private UserSettings createDefaultSettings(
            User user) {

        UserSettings settings =
                createDefaultSettingsObject();

        settings.setUser(user);

        return userSettingsRepository.save(
                settings
        );
    }

    private UserSettings createDefaultSettingsObject() {

        UserSettings settings =
                new UserSettings();

        settings.setEmailNotifications(true);
        settings.setLeaveUpdates(true);
        settings.setTaskUpdates(true);
        settings.setProjectUpdates(true);

        settings.setTheme("SYSTEM");
        settings.setLanguage("English");
        settings.setDateFormat("DD/MM/YYYY");

        return settings;
    }

    private UserSettingsDto convertToDto(
            User user,
            Employee employee,
            UserSettings settings) {

        UserSettingsDto dto =
                new UserSettingsDto();

        dto.setId(settings.getId());

        String firstName =
                user.getFirstName() != null
                        ? user.getFirstName().trim()
                        : "";

        String lastName =
                user.getLastName() != null
                        ? user.getLastName().trim()
                        : "";

        String fullName =
                (firstName + " " + lastName).trim();

        dto.setFullName(fullName);

        dto.setEmail(user.getEmail());

        dto.setPhoneNumber(
                employee != null
                        && employee.getPhoneNumber() != null
                        ? employee.getPhoneNumber()
                        : ""
        );

        dto.setAccountStatus(
                user.isActive()
        );

        dto.setEmailNotifications(
                settings.isEmailNotifications()
        );

        dto.setLeaveUpdates(
                settings.isLeaveUpdates()
        );

        dto.setTaskUpdates(
                settings.isTaskUpdates()
        );

        dto.setProjectUpdates(
                settings.isProjectUpdates()
        );

        dto.setTheme(
                settings.getTheme()
        );

        dto.setLanguage(
                settings.getLanguage()
        );

        dto.setDateFormat(
                settings.getDateFormat()
        );

        return dto;
    }
}