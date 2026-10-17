package com.example.emps.service;

import com.example.emps.dto.ChangePasswordRequest;
import com.example.emps.dto.UserSettingsDto;

public interface SettingsService {

    UserSettingsDto getMySettings();

    UserSettingsDto updateMySettings(UserSettingsDto settingsDto);

    void changePassword(
            String email,
            ChangePasswordRequest request
    );
}