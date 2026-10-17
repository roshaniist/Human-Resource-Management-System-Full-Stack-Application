package com.example.emps.service;

import com.example.emps.dto.ProfileDto;

public interface ProfileService {

    ProfileDto getMyProfile();

    ProfileDto updateMyProfile(ProfileDto profileDto);
}