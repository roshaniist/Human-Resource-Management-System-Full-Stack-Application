package com.example.emps.service.impl;

import com.example.emps.dto.TaskCommentDto;
import com.example.emps.dto.TaskDto;
import com.example.emps.dto.TaskEmployeeDto;
import com.example.emps.dto.TaskStatusUpdateRequest;
import com.example.emps.entity.Employee;
import com.example.emps.entity.Project;
import com.example.emps.entity.Task;
import com.example.emps.entity.TaskComment;
import com.example.emps.entity.User;
import com.example.emps.enums.NotificationType;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.ProjectRepository;
import com.example.emps.repository.TaskCommentRepository;
import com.example.emps.repository.TaskRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.NotificationService;
import com.example.emps.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            TaskCommentRepository taskCommentRepository,
            ProjectRepository projectRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public TaskDto createTask(TaskDto taskDto) {

        if (taskDto.getProjectId() == null) {
            throw new RuntimeException("Project is required");
        }

        Project project =
                projectRepository.findById(taskDto.getProjectId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Project not found"
                                ));

        if (isManager()) {
            checkManagerProjectAccess(project);
        }

        if (taskDto.getEmployeeIds() == null ||
                taskDto.getEmployeeIds().isEmpty()) {

            throw new RuntimeException(
                    "At least one employee must be assigned"
            );
        }

        List<Employee> employees =
                employeeRepository.findAllById(
                        taskDto.getEmployeeIds()
                );

        if (employees.size() !=
                taskDto.getEmployeeIds().size()) {

            throw new RuntimeException(
                    "One or more employees not found"
            );
        }

        validateProjectEmployees(
                project,
                employees
        );

        Task task = new Task();

        task.setTaskTitle(
                taskDto.getTaskTitle()
        );

        task.setDescription(
                taskDto.getDescription()
        );

        task.setDueDate(
                taskDto.getDueDate()
        );

        task.setStatus(
                taskDto.getStatus()
        );

        task.setProject(project);
        task.setEmployees(employees);

        Task savedTask =
                taskRepository.save(task);

        notifyEmployees(
                employees,
                "New Task Assigned",
                "You have been assigned a new task: "
                        + savedTask.getTaskTitle(),
                savedTask.getId()
        );

        return convertToDto(savedTask);
    }

    @Override
    public TaskDto updateTask(
            Long id,
            TaskDto taskDto) {

        Task existingTask =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found"
                                ));

        if (isEmployee()) {
            throw new RuntimeException(
                    "Employees cannot edit task details. "
                            + "Use the status update option."
            );
        }

        if (taskDto.getProjectId() == null) {
            throw new RuntimeException("Project is required");
        }

        Project project =
                projectRepository.findById(
                        taskDto.getProjectId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Project not found"
                        ));

        if (isManager()) {

            checkManagerTaskAccess(existingTask);
            checkManagerProjectAccess(project);

            if (existingTask.getProject() != null &&
                    !existingTask.getProject()
                            .getId()
                            .equals(project.getId())) {

                throw new RuntimeException(
                        "Manager cannot change task project"
                );
            }
        }

        if (taskDto.getEmployeeIds() == null ||
                taskDto.getEmployeeIds().isEmpty()) {

            throw new RuntimeException(
                    "At least one employee must be assigned"
            );
        }

        List<Employee> employees =
                employeeRepository.findAllById(
                        taskDto.getEmployeeIds()
                );

        if (employees.size() !=
                taskDto.getEmployeeIds().size()) {

            throw new RuntimeException(
                    "One or more employees not found"
            );
        }

        validateProjectEmployees(
                project,
                employees
        );

        existingTask.setTaskTitle(
                taskDto.getTaskTitle()
        );

        existingTask.setDescription(
                taskDto.getDescription()
        );

        existingTask.setDueDate(
                taskDto.getDueDate()
        );

        existingTask.setStatus(
                taskDto.getStatus()
        );

        existingTask.setProject(project);
        existingTask.setEmployees(employees);

        Task updatedTask =
                taskRepository.save(existingTask);

        notifyEmployees(
                updatedTask.getEmployees(),
                "Task Updated",
                "Task '" +
                        updatedTask.getTaskTitle() +
                        "' has been updated.",
                updatedTask.getId()
        );

        return convertToDto(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {

        Task task =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found"
                                ));

        if (isEmployee()) {
            checkEmployeeAccess(task);
        }

        if (isManager()) {
            checkManagerTaskAccess(task);
        }

        return convertToDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksForCurrentUser() {

        if (isEmployee()) {

            Employee employee =
                    getLoggedInEmployee();

            return taskRepository
                    .findByEmployeesId(employee.getId())
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        if (isManager()) {

            Employee manager =
                    getLoggedInEmployee();

            List<Project> managedProjects =
                    projectRepository.findByManager_Id(
                            manager.getId()
                    );

            if (managedProjects.isEmpty()) {
                return new ArrayList<>();
            }

            return taskRepository.findAll()
                    .stream()
                    .filter(task ->
                            task.getProject() != null &&
                                    task.getProject().getManager() != null &&
                                    task.getProject().getManager().getId() != null &&
                                    task.getProject().getManager().getId()
                                            .equals(manager.getId())
                    )
                    .map(this::convertToDto)
                    .toList();
        }

        if (isAdmin() || isHr()) {

            return taskRepository.findAll()
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        throw new RuntimeException(
                "You are not authorized to view tasks"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getMyTasks() {

        Employee employee =
                getLoggedInEmployee();

        return taskRepository
                .findByEmployeesId(employee.getId())
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void deleteTask(Long id) {

        if (!isAdmin()) {
            throw new RuntimeException(
                    "Only Admin can delete tasks"
            );
        }

        Task task =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found"
                                )
                        );

        List<Employee> assignedEmployees =
                task.getEmployees() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(
                        task.getEmployees()
                );

        String taskTitle =
                task.getTaskTitle();

        Long taskId =
                task.getId();

        taskRepository.delete(task);

        notifyEmployees(
                assignedEmployees,
                "Task Deleted",
                "Task '" +
                        taskTitle +
                        "' has been deleted.",
                taskId
        );
    }

    @Override
    public TaskDto updateTaskStatusAndComment(
            Long taskId,
            TaskStatusUpdateRequest request) {

        if (!isEmployee()) {

            throw new RuntimeException(
                    "Only employees can use this operation"
            );
        }

        if (request == null) {

            throw new RuntimeException(
                    "Status update request is required"
            );
        }

        if (request.getStatus() == null) {

            throw new RuntimeException(
                    "Task status is required"
            );
        }

        if (request.getComment() == null ||
                request.getComment().trim().isEmpty()) {

            throw new RuntimeException(
                    "Comment is required"
            );
        }

        if (request.getComment().trim().length() > 1000) {

            throw new RuntimeException(
                    "Comment cannot exceed 1000 characters"
            );
        }

        Task task =
                taskRepository.findById(taskId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found"
                                )
                        );

        Employee employee =
                getLoggedInEmployee();

        boolean assignedToEmployee =
                task.getEmployees() != null &&
                        task.getEmployees()
                                .stream()
                                .anyMatch(
                                        assignedEmployee ->
                                                assignedEmployee != null &&
                                                        assignedEmployee.getId() != null &&
                                                        assignedEmployee.getId()
                                                                .equals(
                                                                        employee.getId()
                                                                )
                                );

        if (!assignedToEmployee) {

            throw new RuntimeException(
                    "You can update only your assigned tasks"
            );
        }

        task.setStatus(
                request.getStatus()
        );

        Task updatedTask =
                taskRepository.save(task);

        TaskComment taskComment =
                new TaskComment();

        taskComment.setTask(
                updatedTask
        );

        taskComment.setEmployee(
                employee
        );

        taskComment.setComment(
                request.getComment().trim()
        );

        TaskComment savedComment =
                taskCommentRepository.save(
                        taskComment
                );

        notifyManagerAboutTaskUpdate(
                employee,
                updatedTask,
                savedComment
        );

        return convertToDto(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskCommentDto> getTaskComments(
            Long taskId) {

        Task task =
                taskRepository.findById(taskId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Task not found"
                                )
                        );

        if (isEmployee()) {
            checkEmployeeAccess(task);
        }

        if (isManager()) {
            checkManagerTaskAccess(task);
        }

        return taskCommentRepository
                .findByTaskIdOrderByCreatedAtDesc(
                        taskId
                )
                .stream()
                .map(this::convertToCommentDto)
                .toList();
    }

    private void checkManagerProjectAccess(
            Project project) {

        if (project == null) {
            throw new RuntimeException(
                    "Project not found"
            );
        }

        Employee manager =
                getLoggedInEmployee();

        if (project.getManager() == null ||
                project.getManager().getId() == null ||
                !project.getManager()
                        .getId()
                        .equals(manager.getId())) {

            throw new RuntimeException(
                    "You can access only your managed projects"
            );
        }
    }

    private void checkManagerTaskAccess(
            Task task) {

        if (task == null ||
                task.getProject() == null) {

            throw new RuntimeException(
                    "Task project not found"
            );
        }

        checkManagerProjectAccess(
                task.getProject()
        );
    }

    private void validateProjectEmployees(
            Project project,
            List<Employee> employees) {

        if (project == null) {
            throw new RuntimeException(
                    "Project is required"
            );
        }

        List<Employee> projectEmployees =
                project.getEmployees();

        if (projectEmployees == null ||
                projectEmployees.isEmpty()) {

            throw new RuntimeException(
                    "No employees are assigned to this project"
            );
        }

        boolean invalidEmployee =
                employees.stream()
                        .anyMatch(employee ->
                                employee == null ||
                                        employee.getId() == null ||
                                        projectEmployees
                                                .stream()
                                                .noneMatch(
                                                        projectEmployee ->
                                                                projectEmployee != null &&
                                                                        projectEmployee.getId() != null &&
                                                                        projectEmployee.getId()
                                                                                .equals(
                                                                                        employee.getId()
                                                                                )
                                                )
                        );

        if (invalidEmployee) {

            throw new RuntimeException(
                    "Employees can be assigned only from the selected project"
            );
        }
    }

    private void notifyManagerAboutTaskUpdate(
            Employee employee,
            Task task,
            TaskComment comment) {

        if (employee == null ||
                task == null ||
                task.getProject() == null) {
            return;
        }

        Project project =
                task.getProject();

        Employee manager =
                project.getManager();

        if (manager == null ||
                manager.getUser() == null ||
                manager.getUser().getId() == null) {
            return;
        }

        String employeeName =
                employee.getEmployeeName() != null
                        ? employee.getEmployeeName()
                        : "Employee";

        String message =
                employeeName +
                        " updated task '" +
                        task.getTaskTitle() +
                        "'. Status: " +
                        task.getStatus() +
                        ". Comment: " +
                        comment.getComment();

        notificationService.createNotification(
                manager.getUser().getId(),
                "Task Progress Update",
                message,
                NotificationType.TASK,
                "TASK",
                task.getId()
        );
    }

    private void notifyEmployees(
            List<Employee> employees,
            String title,
            String message,
            Long taskId) {

        if (employees == null ||
                employees.isEmpty()) {
            return;
        }

        for (Employee employee : employees) {

            if (employee == null ||
                    employee.getUser() == null ||
                    employee.getUser().getId() == null) {
                continue;
            }

            notificationService.createNotification(
                    employee.getUser().getId(),
                    title,
                    message,
                    NotificationType.TASK,
                    "TASK",
                    taskId
            );
        }
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null) {

            throw new RuntimeException(
                    "Authentication required"
            );
        }

        return userRepository
                .findByEmail(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );
    }

    private Employee getLoggedInEmployee() {

        User user =
                getLoggedInUser();

        return employeeRepository
                .findByUserId(
                        user.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found"
                        )
                );
    }

    private boolean isEmployee() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null &&
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals(
                                                "ROLE_EMPLOYEE"
                                        )
                        );
    }

    private boolean isManager() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null &&
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals(
                                                "ROLE_MANAGER"
                                        )
                        );
    }

    private boolean isAdmin() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null &&
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals(
                                                "ROLE_ADMIN"
                                        )
                        );
    }

    private boolean isHr() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null &&
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals(
                                                "ROLE_HR"
                                        )
                        );
    }

    private void checkEmployeeAccess(
            Task task) {

        User loggedInUser =
                getLoggedInUser();

        boolean assignedToEmployee =
                task.getEmployees() != null &&
                        task.getEmployees()
                                .stream()
                                .anyMatch(employee ->
                                        employee != null &&
                                                employee.getUser() != null &&
                                                employee.getUser().getId() != null &&
                                                employee.getUser()
                                                        .getId()
                                                        .equals(
                                                                loggedInUser.getId()
                                                        )
                                );

        if (!assignedToEmployee) {

            throw new RuntimeException(
                    "You can access only your assigned tasks"
            );
        }
    }

    private TaskDto convertToDto(
            Task task) {

        TaskDto dto =
                new TaskDto();

        dto.setId(
                task.getId()
        );

        dto.setTaskTitle(
                task.getTaskTitle()
        );

        dto.setDescription(
                task.getDescription()
        );

        dto.setDueDate(
                task.getDueDate()
        );

        dto.setStatus(
                task.getStatus()
        );

        if (task.getProject() != null) {

            dto.setProjectId(
                    task.getProject().getId()
            );
        }

        List<TaskEmployeeDto> employeeDtos =
                new ArrayList<>();

        if (task.getEmployees() != null) {

            for (Employee employee :
                    task.getEmployees()) {

                String email = null;

                if (employee.getUser() != null) {

                    email =
                            employee.getUser()
                                    .getEmail();
                }

                employeeDtos.add(
                        new TaskEmployeeDto(
                                employee.getId(),
                                employee.getEmployeeName(),
                                email,
                                employee.getDesignation()
                        )
                );
            }
        }

        dto.setEmployees(
                employeeDtos
        );

        dto.setEmployeeIds(
                task.getEmployees() == null
                        ? new ArrayList<>()
                        : task.getEmployees()
                        .stream()
                        .map(Employee::getId)
                        .toList()
        );

        return dto;
    }

    private TaskCommentDto convertToCommentDto(
            TaskComment comment) {

        TaskCommentDto dto =
                new TaskCommentDto();

        dto.setId(
                comment.getId()
        );

        dto.setTaskId(
                comment.getTask().getId()
        );

        dto.setEmployeeId(
                comment.getEmployee().getId()
        );

        dto.setEmployeeName(
                comment.getEmployee()
                        .getEmployeeName()
        );

        dto.setComment(
                comment.getComment()
        );

        dto.setCreatedAt(
                comment.getCreatedAt()
        );

        return dto;
    }
}