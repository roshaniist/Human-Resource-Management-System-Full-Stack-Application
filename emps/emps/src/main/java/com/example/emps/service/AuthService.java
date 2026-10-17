package com.example.emps.service;

import com.example.emps.dto.AuthResponse;
import com.example.emps.dto.ChangePasswordRequest;
import com.example.emps.dto.ForgotPasswordRequest;
import com.example.emps.dto.LoginRequest;
import com.example.emps.dto.RegisterRequest;
import com.example.emps.dto.ResetPasswordRequest;
import com.example.emps.dto.VerifyOtpRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void verifyOtp(VerifyOtpRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(
            String email,
            ChangePasswordRequest request
    );
}