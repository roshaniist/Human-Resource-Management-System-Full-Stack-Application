package com.example.emps.service.impl;

import com.example.emps.dto.UserDto;
import com.example.emps.entity.User;
import com.example.emps.enums.RoleType;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getITSupportUsers() {

        return userRepository
                .findAll()
                .stream()
                .filter(user ->
                        user.getRole() != null &&
                                user.getRole().getRoleName()
                                        == RoleType.IT_SUPPORT
                )
                .filter(User::isActive)
                .map(this::convertToDto)
                .toList();
    }

    private UserDto convertToDto(User user) {

        UserDto dto = new UserDto();

        dto.setId(user.getId());

        dto.setName(
                user.getFirstName() + " " +
                        user.getLastName()
        );

        dto.setEmail(user.getEmail());

        if (user.getRole() != null) {
            dto.setRole(
                    user.getRole().getRoleName()
            );
        }

        dto.setActive(user.isActive());

        return dto;
    }
}