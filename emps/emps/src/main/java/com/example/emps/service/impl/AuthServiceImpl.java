package com.example.emps.service.impl;

import com.example.emps.dto.AuthResponse;
import com.example.emps.dto.ChangePasswordRequest;
import com.example.emps.dto.ForgotPasswordRequest;
import com.example.emps.dto.LoginRequest;
import com.example.emps.dto.RegisterRequest;
import com.example.emps.dto.ResetPasswordRequest;
import com.example.emps.dto.VerifyOtpRequest;
import com.example.emps.entity.PasswordResetOtp;
import com.example.emps.entity.Role;
import com.example.emps.entity.User;
import com.example.emps.enums.RoleType;
import com.example.emps.repository.PasswordResetOtpRepository;
import com.example.emps.repository.RoleRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.security.JwtService;
import com.example.emps.service.AuthService;
import com.example.emps.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordResetOtpRepository passwordResetOtpRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordResetOtpRepository = passwordResetOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository
                .findByRoleName(RoleType.EMPLOYEE)
                .orElseThrow(() ->
                        new RuntimeException("EMPLOYEE role not found"));

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(role);
        user.setActive(true);

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                token,
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                "User registered successfully"
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        ));

        if (!user.isActive()) {
            throw new RuntimeException(
                    "User account is inactive"
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                token,
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                "Login successful"
        );
    }

    @Override
    public void forgotPassword(
            ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        passwordResetOtpRepository
                .findByUserEmail(user.getEmail())
                .ifPresent(
                        passwordResetOtpRepository::delete
                );

        String otp = String.format(
                "%06d",
                new Random().nextInt(1_000_000)
        );

        PasswordResetOtp passwordResetOtp =
                new PasswordResetOtp();

        passwordResetOtp.setOtp(otp);

        passwordResetOtp.setExpiryTime(
                LocalDateTime.now().plusMinutes(10)
        );

        passwordResetOtp.setVerified(false);
        passwordResetOtp.setUser(user);

        passwordResetOtpRepository.save(
                passwordResetOtp
        );

        emailService.sendOtp(
                user.getEmail(),
                otp
        );
    }

    @Override
    public void verifyOtp(
            VerifyOtpRequest request) {

        PasswordResetOtp passwordResetOtp =
                passwordResetOtpRepository
                        .findByOtpAndUserEmail(
                                request.getOtp(),
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid OTP"
                                ));

        if (passwordResetOtp
                .getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP has expired"
            );
        }

        if (passwordResetOtp.isVerified()) {

            throw new RuntimeException(
                    "OTP already verified"
            );
        }

        passwordResetOtp.setVerified(true);

        passwordResetOtpRepository.save(
                passwordResetOtp
        );
    }

    @Override
    public void resetPassword(
            ResetPasswordRequest request) {

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException(
                    "New password and confirm password do not match"
            );
        }

        PasswordResetOtp passwordResetOtp =
                passwordResetOtpRepository
                        .findByUserEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "OTP verification required"
                                ));

        if (!passwordResetOtp.isVerified()) {

            throw new RuntimeException(
                    "Please verify OTP first"
            );
        }

        if (passwordResetOtp
                .getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP has expired"
            );
        }

        User user = passwordResetOtp.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        passwordResetOtpRepository.delete(
                passwordResetOtp
        );
    }

    @Override
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException(
                    "New password and confirm password do not match"
            );
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }
}