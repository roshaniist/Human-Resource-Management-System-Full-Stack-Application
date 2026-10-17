package com.example.emps.dto;

import com.example.emps.enums.RoleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String name;

    private String email;

    private RoleType role;

    private boolean active;
}