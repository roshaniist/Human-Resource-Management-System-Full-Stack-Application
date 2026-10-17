package com.example.emps.service.impl;

import com.example.emps.dto.EmployeeDto;
import com.example.emps.entity.Department;
import com.example.emps.entity.Employee;
import com.example.emps.entity.Role;
import com.example.emps.entity.User;
import com.example.emps.enums.RoleType;
import com.example.emps.repository.DepartmentRepository;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.RoleRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.EmployeeService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            RoleRepository roleRepository) {

        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        if (employeeRepository.existsByEmployeeCode(
                employeeDto.getEmployeeCode())) {

            throw new RuntimeException(
                    "Employee code already exists"
            );
        }

        User user = userRepository.findById(
                employeeDto.getUserId()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        Department department = departmentRepository.findById(
                employeeDto.getDepartmentId()
        ).orElseThrow(() ->
                new RuntimeException("Department not found")
        );

        RoleType selectedRole = validateEmployeeRole(
                employeeDto.getRole()
        );

        Role roleEntity = roleRepository
                .findByRoleName(selectedRole)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Selected role not found"
                        )
                );

        user.setRole(roleEntity);

        userRepository.save(user);

        Employee employee = new Employee();

        employee.setEmployeeCode(
                employeeDto.getEmployeeCode()
        );

        employee.setEmployeeName(
                employeeDto.getEmployeeName()
        );

        employee.setGender(
                employeeDto.getGender()
        );

        employee.setPhoneNumber(
                employeeDto.getPhoneNumber()
        );

        employee.setDesignation(
                employeeDto.getDesignation()
        );

        employee.setSalary(
                employeeDto.getSalary()
        );

        employee.setJoiningDate(
                employeeDto.getJoiningDate()
        );

        employee.setUser(user);

        employee.setDepartment(department);

        Employee savedEmployee =
                employeeRepository.save(employee);

        return convertToDto(savedEmployee);
    }

    @Override
    public EmployeeDto updateEmployee(
            Long id,
            EmployeeDto employeeDto) {

        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

        User loggedInUser = getLoggedInUser();

        String role = loggedInUser
                .getRole()
                .getRoleName()
                .name();

        if ("EMPLOYEE".equals(role)) {

            if (employee.getUser() == null ||
                    !employee.getUser()
                            .getId()
                            .equals(loggedInUser.getId())) {

                throw new RuntimeException(
                        "You can update only your own profile"
                );
            }

            employee.setPhoneNumber(
                    employeeDto.getPhoneNumber()
            );

            employee.setDesignation(
                    employeeDto.getDesignation()
            );

            employee.setJoiningDate(
                    employeeDto.getJoiningDate()
            );
        }

        else if ("ADMIN".equals(role) ||
                "HR".equals(role)) {

            User user =
                    userRepository.findById(
                            employeeDto.getUserId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "User not found"
                            )
                    );

            Department department =
                    departmentRepository.findById(
                            employeeDto.getDepartmentId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Department not found"
                            )
                    );

            RoleType selectedRole = validateEmployeeRole(
                    employeeDto.getRole()
            );

            Role roleEntity = roleRepository
                    .findByRoleName(selectedRole)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Selected role not found"
                            )
                    );

            user.setRole(roleEntity);

            userRepository.save(user);

            employee.setEmployeeCode(
                    employeeDto.getEmployeeCode()
            );

            employee.setEmployeeName(
                    employeeDto.getEmployeeName()
            );

            employee.setGender(
                    employeeDto.getGender()
            );

            employee.setPhoneNumber(
                    employeeDto.getPhoneNumber()
            );

            employee.setDesignation(
                    employeeDto.getDesignation()
            );

            employee.setSalary(
                    employeeDto.getSalary()
            );

            employee.setJoiningDate(
                    employeeDto.getJoiningDate()
            );

            employee.setUser(user);

            employee.setDepartment(department);
        }

        else {

            throw new RuntimeException(
                    "You are not authorized to update employee data"
            );
        }

        Employee updatedEmployee =
                employeeRepository.save(employee);

        return convertToDto(updatedEmployee);
    }

    @Override
    public EmployeeDto getMyEmployee() {

        User loggedInUser = getLoggedInUser();

        Employee employee =
                employeeRepository
                        .findByUserId(loggedInUser.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee record not found for logged-in user"
                                )
                        );

        return convertToDto(employee);
    }

    @Override
    public List<EmployeeDto> getMyTeam() {

        User loggedInUser = getLoggedInUser();

        Employee manager =
                employeeRepository
                        .findByUserId(loggedInUser.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee record not found for logged-in manager"
                                )
                        );

        if (manager.getDepartment() == null) {

            throw new RuntimeException(
                    "Manager is not assigned to any department"
            );
        }

        Long departmentId =
                manager.getDepartment().getId();

        return employeeRepository
                .findByDepartmentId(departmentId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {

        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

        User loggedInUser = getLoggedInUser();

        String role = loggedInUser
                .getRole()
                .getRoleName()
                .name();

        if ("EMPLOYEE".equals(role)) {

            if (employee.getUser() == null ||
                    !employee.getUser()
                            .getId()
                            .equals(loggedInUser.getId())) {

                throw new RuntimeException(
                        "You can view only your own profile"
                );
            }
        }

        if ("MANAGER".equals(role)) {

            Employee manager =
                    employeeRepository
                            .findByUserId(loggedInUser.getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Manager employee record not found"
                                    )
                            );

            if (manager.getDepartment() == null ||
                    employee.getDepartment() == null ||
                    !employee.getDepartment()
                            .getId()
                            .equals(
                                    manager.getDepartment().getId()
                            )) {

                throw new RuntimeException(
                        "You can view only employees from your team"
                );
            }
        }

        return convertToDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<EmployeeDto> getAllManagers() {

        return employeeRepository
                .findByUserRoleRoleName(RoleType.MANAGER)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void deleteEmployee(Long id) {

        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

        employeeRepository.delete(employee);
    }

    private RoleType validateEmployeeRole(
            RoleType selectedRole) {

        if (selectedRole == null) {

            throw new RuntimeException(
                    "Role is required"
            );
        }

        if (selectedRole == RoleType.ADMIN) {

            throw new RuntimeException(
                    "ADMIN role cannot be assigned through Employee Management"
            );
        }

        if (selectedRole != RoleType.HR &&
                selectedRole != RoleType.MANAGER &&
                selectedRole != RoleType.EMPLOYEE &&
                selectedRole != RoleType.IT_SUPPORT) {

            throw new RuntimeException(
                    "Invalid employee role"
            );
        }

        return selectedRole;
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null) {

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

    private EmployeeDto convertToDto(
            Employee employee) {

        EmployeeDto dto = new EmployeeDto();

        dto.setId(
                employee.getId()
        );

        dto.setEmployeeCode(
                employee.getEmployeeCode()
        );

        dto.setEmployeeName(
                employee.getEmployeeName()
        );

        dto.setGender(
                employee.getGender()
        );

        dto.setPhoneNumber(
                employee.getPhoneNumber()
        );

        dto.setDesignation(
                employee.getDesignation()
        );

        dto.setSalary(
                employee.getSalary()
        );

        dto.setJoiningDate(
                employee.getJoiningDate()
        );

        if (employee.getUser() != null) {

            dto.setUserId(
                    employee.getUser().getId()
            );

            if (employee.getUser().getRole() != null) {

                dto.setRole(
                        employee.getUser()
                                .getRole()
                                .getRoleName()
                );
            }
        }

        if (employee.getDepartment() != null) {

            dto.setDepartmentId(
                    employee.getDepartment().getId()
            );
        }

        return dto;
    }
}