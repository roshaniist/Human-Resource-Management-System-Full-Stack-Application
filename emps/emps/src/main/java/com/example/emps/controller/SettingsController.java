package com.example.emps.controller;

import com.example.emps.dto.ChangePasswordRequest;
import com.example.emps.dto.UserSettingsDto;
import com.example.emps.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserSettingsDto> getMySettings() {
        return ResponseEntity.ok(
                settingsService.getMySettings()
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserSettingsDto> updateMySettings(
            @Valid @RequestBody UserSettingsDto settingsDto) {

        return ResponseEntity.ok(
                settingsService.updateMySettings(settingsDto)
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        settingsService.changePassword(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }
}