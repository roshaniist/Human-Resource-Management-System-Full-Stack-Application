package com.example.emps.service.impl;

import com.example.emps.dto.ProjectDto;
import com.example.emps.dto.ProjectEmployeeDto;
import com.example.emps.entity.Employee;
import com.example.emps.entity.Project;
import com.example.emps.entity.User;
import com.example.emps.enums.NotificationType;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.ProjectRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.NotificationService;
import com.example.emps.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {

        Project project = new Project();

        project.setProjectName(projectDto.getProjectName());
        project.setDescription(projectDto.getDescription());
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setStatus(projectDto.getStatus());

        if (projectDto.getManagerId() != null) {

            Employee manager =
                    employeeRepository.findById(
                            projectDto.getManagerId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Manager not found"
                            ));

            project.setManager(manager);
        }

        if (projectDto.getEmployeeIds() != null &&
                !projectDto.getEmployeeIds().isEmpty()) {

            List<Employee> employees =
                    employeeRepository.findAllById(
                            projectDto.getEmployeeIds()
                    );

            project.setEmployees(employees);
        }

        Project savedProject =
                projectRepository.save(project);

        notifyProjectUsers(
                savedProject,
                "New Project Assigned",
                "You have been assigned to project: "
                        + savedProject.getProjectName()
        );

        return convertToDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectDto updateProject(
            Long id,
            ProjectDto projectDto) {

        Project existingProject =
                projectRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        existingProject.setProjectName(
                projectDto.getProjectName()
        );

        existingProject.setDescription(
                projectDto.getDescription()
        );

        existingProject.setStartDate(
                projectDto.getStartDate()
        );

        existingProject.setEndDate(
                projectDto.getEndDate()
        );

        existingProject.setStatus(
                projectDto.getStatus()
        );

        if (projectDto.getManagerId() != null) {

            Employee manager =
                    employeeRepository.findById(
                            projectDto.getManagerId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Manager not found"
                            ));

            existingProject.setManager(manager);

        } else {

            existingProject.setManager(null);
        }

        if (projectDto.getEmployeeIds() != null) {

            List<Employee> employees =
                    employeeRepository.findAllById(
                            projectDto.getEmployeeIds()
                    );

            existingProject.setEmployees(employees);

        } else {

            existingProject.getEmployees().clear();
        }

        Project updatedProject =
                projectRepository.save(existingProject);

        notifyProjectUsers(
                updatedProject,
                "Project Updated",
                "Project '" +
                        updatedProject.getProjectName() +
                        "' has been updated."
        );

        return convertToDto(updatedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long id) {

        Project project =
                projectRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        return convertToDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getMyProjectsByEmail(
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        Employee employee =
                employeeRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                ));

        if (user.getRole() != null &&
                user.getRole().getRoleName() != null &&
                user.getRole().getRoleName().name().equals("MANAGER")) {

            return projectRepository
                    .findByManager_Id(employee.getId())
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        return projectRepository
                .findByEmployees_Id(employee.getId())
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {

        Project project =
                projectRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        List<Employee> projectEmployees =
                project.getEmployees() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(project.getEmployees());

        Employee manager = project.getManager();

        String projectName = project.getProjectName();
        Long projectId = project.getId();

        projectRepository.delete(project);

        notifyProjectUsers(
                manager,
                projectEmployees,
                "Project Deleted",
                "Project '" + projectName +
                        "' has been deleted.",
                projectId
        );
    }

    private void notifyProjectUsers(
            Project project,
            String title,
            String message) {

        Employee manager = project.getManager();

        List<Employee> employees =
                project.getEmployees() == null
                        ? new ArrayList<>()
                        : project.getEmployees();

        notifyProjectUsers(
                manager,
                employees,
                title,
                message,
                project.getId()
        );
    }

    private void notifyProjectUsers(
            Employee manager,
            List<Employee> employees,
            String title,
            String message,
            Long projectId) {

        Set<Long> notifiedUserIds = new HashSet<>();

        if (manager != null &&
                manager.getUser() != null &&
                manager.getUser().getId() != null) {

            Long managerUserId =
                    manager.getUser().getId();

            notifiedUserIds.add(managerUserId);

            notificationService.createNotification(
                    managerUserId,
                    title,
                    message,
                    NotificationType.PROJECT,
                    "PROJECT",
                    projectId
            );
        }

        if (employees == null || employees.isEmpty()) {
            return;
        }

        for (Employee employee : employees) {

            if (employee == null ||
                    employee.getUser() == null ||
                    employee.getUser().getId() == null) {
                continue;
            }

            Long userId =
                    employee.getUser().getId();

            if (notifiedUserIds.contains(userId)) {
                continue;
            }

            notifiedUserIds.add(userId);

            notificationService.createNotification(
                    userId,
                    title,
                    message,
                    NotificationType.PROJECT,
                    "PROJECT",
                    projectId
            );
        }
    }

    private ProjectDto convertToDto(Project project) {

        ProjectDto dto = new ProjectDto();

        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setStatus(project.getStatus());

        Employee manager = project.getManager();

        if (manager != null) {

            String managerName =
                    manager.getEmployeeName() != null
                            ? manager.getEmployeeName()
                            : "";

            String managerEmail =
                    manager.getUser() != null
                            ? manager.getUser().getEmail()
                            : "";

            String managerPhone =
                    manager.getPhoneNumber() != null
                            ? manager.getPhoneNumber()
                            : "";

            String managerEmployeeCode =
                    manager.getEmployeeCode() != null
                            ? manager.getEmployeeCode()
                            : "";

            String managerDesignation =
                    manager.getDesignation() != null
                            ? manager.getDesignation()
                            : "";

            String managerDepartment =
                    manager.getDepartment() != null &&
                            manager.getDepartment().getDepartmentName() != null
                            ? manager.getDepartment().getDepartmentName()
                            : "";

            dto.setManagerId(manager.getId());

            dto.setManager(
                    new ProjectEmployeeDto(
                            manager.getId(),
                            managerEmployeeCode,
                            managerPhone,
                            managerName,
                            managerEmail,
                            managerDesignation,
                            managerDepartment
                    )
            );

        } else {

            dto.setManagerId(null);
            dto.setManager(null);
        }

        List<Employee> employees =
                project.getEmployees();

        if (employees != null && !employees.isEmpty()) {

            dto.setEmployeeIds(
                    employees.stream()
                            .map(Employee::getId)
                            .toList()
            );

            dto.setEmployees(
                    employees.stream()
                            .map(employee -> {

                                String employeeName =
                                        employee.getEmployeeName() != null
                                                ? employee.getEmployeeName()
                                                : "";

                                String employeeCode =
                                        employee.getEmployeeCode() != null
                                                ? employee.getEmployeeCode()
                                                : "";

                                String phoneNumber =
                                        employee.getPhoneNumber() != null
                                                ? employee.getPhoneNumber()
                                                : "";

                                String email =
                                        employee.getUser() != null
                                                ? employee.getUser().getEmail()
                                                : "";

                                String designation =
                                        employee.getDesignation() != null
                                                ? employee.getDesignation()
                                                : "";

                                String departmentName =
                                        employee.getDepartment() != null &&
                                                employee.getDepartment().getDepartmentName() != null
                                                ? employee.getDepartment().getDepartmentName()
                                                : "";

                                return new ProjectEmployeeDto(
                                        employee.getId(),
                                        employeeCode,
                                        phoneNumber,
                                        employeeName,
                                        email,
                                        designation,
                                        departmentName
                                );
                            })
                            .toList()
            );

        } else {

            dto.setEmployeeIds(List.of());
            dto.setEmployees(List.of());
        }

        return dto;
    }
}