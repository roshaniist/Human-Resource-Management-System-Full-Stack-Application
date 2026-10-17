package com.example.emps.service.impl;

import com.example.emps.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtp(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("EPMS - Password Reset OTP");

        message.setText(
                "Hello,\n\n"
                        + "Your password reset OTP is: " + otp + "\n\n"
                        + "This OTP is valid for 10 minutes.\n\n"
                        + "If you did not request a password reset, "
                        + "please ignore this email.\n\n"
                        + "Regards,\n"
                        + "EPMS Team"
        );

        mailSender.send(message);
    }
}