package com.example.emps.controller;

import com.example.emps.dto.ProfileDto;
import com.example.emps.service.ProfileService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    @GetMapping("/me")
    public ResponseEntity<ProfileDto> getMyProfile() {

        return ResponseEntity.ok(
                profileService.getMyProfile()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    @PutMapping("/me")
    public ResponseEntity<ProfileDto> updateMyProfile(
            @Valid @RequestBody ProfileDto profileDto) {

        return ResponseEntity.ok(
                profileService.updateMyProfile(profileDto)
        );
    }
}