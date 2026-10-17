package com.example.emps.dto;

import com.example.emps.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private Long userId;

    private String token;

    private String name;

    private String email;

    private RoleType role;

    private String message;
}