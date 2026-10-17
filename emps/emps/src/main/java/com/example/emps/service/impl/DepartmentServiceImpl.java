package com.example.emps.service.impl;

import com.example.emps.dto.DepartmentDto;
import com.example.emps.entity.Department;
import com.example.emps.entity.Employee;
import com.example.emps.entity.User;
import com.example.emps.repository.DepartmentRepository;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.DepartmentService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            EmployeeRepository employeeRepository) {

        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }


    // =========================================================
    // CREATE DEPARTMENT
    // =========================================================

    @Override
    public DepartmentDto createDepartment(
            DepartmentDto departmentDto) {

        // Check department name
        if (departmentRepository.existsByDepartmentName(
                departmentDto.getDepartmentName())) {

            throw new RuntimeException(
                    "Department already exists"
            );
        }

        // Check department code
        if (departmentRepository.existsByDepartmentCode(
                departmentDto.getDepartmentCode())) {

            throw new RuntimeException(
                    "Department code already exists"
            );
        }

        Department department = new Department();

        department.setDepartmentName(
                departmentDto.getDepartmentName()
        );

        department.setDepartmentCode(
                departmentDto.getDepartmentCode()
        );

        department.setDescription(
                departmentDto.getDescription()
        );

        // Set manager
        if (departmentDto.getManagerId() != null) {

            User manager = userRepository
                    .findById(departmentDto.getManagerId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Manager not found"
                            )
                    );

            department.setManager(manager);
        }

        Department savedDepartment =
                departmentRepository.save(department);

        return convertToDto(savedDepartment);
    }


    // =========================================================
    // UPDATE DEPARTMENT
    // =========================================================

    @Override
    public DepartmentDto updateDepartment(
            Long id,
            DepartmentDto departmentDto) {

        Department existingDepartment =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found"
                                )
                        );

        // Check duplicate department name
        if (!existingDepartment.getDepartmentName()
                .equals(departmentDto.getDepartmentName())
                && departmentRepository.existsByDepartmentName(
                departmentDto.getDepartmentName())) {

            throw new RuntimeException(
                    "Department already exists"
            );
        }

        // Check duplicate department code
        if (!existingDepartment.getDepartmentCode()
                .equals(departmentDto.getDepartmentCode())
                && departmentRepository.existsByDepartmentCode(
                departmentDto.getDepartmentCode())) {

            throw new RuntimeException(
                    "Department code already exists"
            );
        }

        existingDepartment.setDepartmentName(
                departmentDto.getDepartmentName()
        );

        existingDepartment.setDepartmentCode(
                departmentDto.getDepartmentCode()
        );

        existingDepartment.setDescription(
                departmentDto.getDescription()
        );

        // Update manager
        if (departmentDto.getManagerId() != null) {

            User manager = userRepository
                    .findById(departmentDto.getManagerId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Manager not found"
                            )
                    );

            existingDepartment.setManager(manager);

        } else {

            existingDepartment.setManager(null);
        }

        Department updatedDepartment =
                departmentRepository.save(existingDepartment);

        return convertToDto(updatedDepartment);
    }


    // =========================================================
    // GET DEPARTMENT BY ID
    // =========================================================

    @Override
    public DepartmentDto getDepartmentById(Long id) {

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found"
                                )
                        );

        return convertToDto(department);
    }


    // =========================================================
    // GET ALL DEPARTMENTS
    // ADMIN / HR / MANAGER
    // =========================================================

    @Override
    public List<DepartmentDto> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }


    // =========================================================
    // GET MY DEPARTMENT
    // EMPLOYEE
    // =========================================================

    @Override
    public DepartmentDto getMyDepartment() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email =
                authentication.getName();

        Employee employee =
                employeeRepository
                        .findByUserEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

        if (employee.getDepartment() == null) {

            throw new RuntimeException(
                    "Employee is not assigned to any department"
            );
        }

        Department department =
                employee.getDepartment();

        return convertToDto(department);
    }


    // =========================================================
    // DELETE DEPARTMENT
    // =========================================================

    @Override
    public void deleteDepartment(Long id) {

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found"
                                )
                        );

        departmentRepository.delete(department);
    }


    // =========================================================
    // CONVERT ENTITY TO DTO
    // =========================================================

    private DepartmentDto convertToDto(
            Department department) {

        DepartmentDto dto = new DepartmentDto();

        dto.setId(
                department.getId()
        );

        dto.setDepartmentName(
                department.getDepartmentName()
        );

        dto.setDepartmentCode(
                department.getDepartmentCode()
        );

        dto.setDescription(
                department.getDescription()
        );


        // Employee count
        long employeeCount =
                employeeRepository.countByDepartmentId(
                        department.getId()
                );

        dto.setEmployeeCount(
                employeeCount
        );


        // Manager details
        if (department.getManager() != null) {

            User manager =
                    department.getManager();

            dto.setManagerId(
                    manager.getId()
            );

            String firstName =
                    manager.getFirstName();

            String lastName =
                    manager.getLastName();

            String managerName =
                    ((firstName != null)
                            ? firstName
                            : "")
                            + " "
                            + ((lastName != null)
                            ? lastName
                            : "");

            dto.setManagerName(
                    managerName.trim()
            );
        }

        return dto;
    }
}