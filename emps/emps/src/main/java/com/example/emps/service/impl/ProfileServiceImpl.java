package com.example.emps.service.impl;

import com.example.emps.dto.ProfileDto;
import com.example.emps.entity.Employee;
import com.example.emps.entity.User;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.ProfileService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public ProfileServiceImpl(
            EmployeeRepository employeeRepository,
            UserRepository userRepository) {

        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProfileDto getMyProfile() {

        User loggedInUser = getLoggedInUser();

        Employee employee = employeeRepository
                .findByUserId(loggedInUser.getId())
                .orElse(null);

        /*
         * Admin may not have an employee record.
         * In that case, show profile using users table.
         */
        if (employee == null) {

            if (loggedInUser.getRole() != null &&
                    loggedInUser.getRole().getRoleName() != null &&
                    loggedInUser.getRole().getRoleName().name().equals("ADMIN")) {

                return convertUserToDto(loggedInUser);
            }

            throw new RuntimeException(
                    "Employee record not found for logged-in user"
            );
        }

        return convertToDto(employee);
    }

    @Override
    public ProfileDto updateMyProfile(ProfileDto profileDto) {

        User loggedInUser = getLoggedInUser();

        Employee employee = employeeRepository
                .findByUserId(loggedInUser.getId())
                .orElse(null);

        /*
         * Admin without employee record does not have
         * employee phone information to update.
         */
        if (employee == null) {

            if (loggedInUser.getRole() != null &&
                    loggedInUser.getRole().getRoleName() != null &&
                    loggedInUser.getRole().getRoleName().name().equals("ADMIN")) {

                throw new RuntimeException(
                        "Admin employee profile is not available for phone number update"
                );
            }

            throw new RuntimeException(
                    "Employee record not found for logged-in user"
            );
        }

        if (profileDto.getPhoneNumber() == null ||
                profileDto.getPhoneNumber().trim().isEmpty()) {

            throw new RuntimeException(
                    "Phone number is required"
            );
        }

        employee.setPhoneNumber(
                profileDto.getPhoneNumber().trim()
        );

        Employee updatedEmployee =
                employeeRepository.save(employee);

        return convertToDto(updatedEmployee);
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );
    }

    private ProfileDto convertUserToDto(User user) {

        ProfileDto dto = new ProfileDto();

        dto.setEmployeeName(
                ((user.getFirstName() == null ? "" : user.getFirstName()) +
                        " " +
                        (user.getLastName() == null ? "" : user.getLastName()))
                        .trim()
        );

        dto.setEmail(
                user.getEmail()
        );

        dto.setRole(
                user.getRole() != null &&
                        user.getRole().getRoleName() != null
                        ? user.getRole().getRoleName().name()
                        : null
        );

        dto.setStatus(
                user.isActive()
                        ? "ACTIVE"
                        : "INACTIVE"
        );

        return dto;
    }

    private ProfileDto convertToDto(Employee employee) {

        ProfileDto dto = new ProfileDto();

        dto.setId(employee.getId());

        dto.setEmployeeCode(
                employee.getEmployeeCode()
        );

        dto.setEmployeeName(
                employee.getEmployeeName()
        );

        if (employee.getUser() != null) {

            User user = employee.getUser();

            String fullName =
                    ((user.getFirstName() == null ? "" : user.getFirstName()) +
                            " " +
                            (user.getLastName() == null ? "" : user.getLastName()))
                            .trim();

            dto.setEmployeeName(
                    fullName.isEmpty()
                            ? employee.getEmployeeName()
                            : fullName
            );

            dto.setEmail(
                    user.getEmail()
            );

            dto.setRole(
                    user.getRole() != null &&
                            user.getRole().getRoleName() != null
                            ? user.getRole().getRoleName().name()
                            : null
            );

            dto.setStatus(
                    user.isActive()
                            ? "ACTIVE"
                            : "INACTIVE"
            );
        }

        dto.setPhoneNumber(
                employee.getPhoneNumber()
        );

        dto.setDesignation(
                employee.getDesignation()
        );

        dto.setJoiningDate(
                employee.getJoiningDate()
        );

        if (employee.getDepartment() != null) {

            dto.setDepartmentName(
                    employee.getDepartment()
                            .getDepartmentName()
            );
        }

        return dto;
    }
}