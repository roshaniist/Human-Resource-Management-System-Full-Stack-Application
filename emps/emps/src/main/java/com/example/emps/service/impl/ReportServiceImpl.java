package com.example.emps.service.impl;

import com.example.emps.dto.report.AttendanceReportDto;
import com.example.emps.dto.report.DepartmentReportDto;
import com.example.emps.dto.report.DocumentReportDto;
import com.example.emps.dto.report.EmployeeReportDto;
import com.example.emps.dto.report.EmployeeReportSummaryDto;
import com.example.emps.dto.report.LeaveReportDto;
import com.example.emps.dto.report.PayrollReportDto;
import com.example.emps.dto.report.PerformanceReportDto;
import com.example.emps.dto.report.ProjectReportDto;
import com.example.emps.dto.report.TaskReportDto;
import com.example.emps.entity.Department;
import com.example.emps.entity.Employee;
import com.example.emps.entity.Project;
import com.example.emps.entity.Task;
import com.example.emps.entity.User;
import com.example.emps.enums.RoleType;
import com.example.emps.repository.AttendanceRepository;
import com.example.emps.repository.DepartmentRepository;
import com.example.emps.repository.EmployeeDocumentRepository;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.LeaveRequestRepository;
import com.example.emps.repository.PayrollRepository;
import com.example.emps.repository.PerformanceReviewRepository;
import com.example.emps.repository.ProjectRepository;
import com.example.emps.repository.TaskRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.ReportService;
import com.example.emps.specification.EmployeeReportSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeDocumentRepository employeeDocumentRepository;
    private final UserRepository userRepository;

    public ReportServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            AttendanceRepository attendanceRepository,
            LeaveRequestRepository leaveRequestRepository,
            PayrollRepository payrollRepository,
            PerformanceReviewRepository performanceReviewRepository,
            EmployeeDocumentRepository employeeDocumentRepository,
            UserRepository userRepository) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.payrollRepository = payrollRepository;
        this.performanceReviewRepository = performanceReviewRepository;
        this.employeeDocumentRepository = employeeDocumentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeReportDto> getEmployeeReport() {

        User loggedInUser = getLoggedInUser();

        if (isManager(loggedInUser)) {

            Long managerDepartmentId =
                    getManagerDepartmentId(loggedInUser);

            if (managerDepartmentId == null) {
                return List.of();
            }

            Specification<Employee> specification =
                    EmployeeReportSpecification.filter(
                            null,
                            managerDepartmentId,
                            null,
                            null,
                            null,
                            null
                    );

            return employeeRepository
                    .findAll(specification)
                    .stream()
                    .map(this::convertEmployeeToReportDto)
                    .toList();
        }

        return employeeRepository
                .findAll()
                .stream()
                .map(this::convertEmployeeToReportDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeReportDto> getEmployeeReport(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active) {

        User loggedInUser = getLoggedInUser();

        if (isManager(loggedInUser)) {

            Long managerDepartmentId =
                    getManagerDepartmentId(loggedInUser);

            if (managerDepartmentId == null) {
                return List.of();
            }

            departmentId = managerDepartmentId;
        }

        Specification<Employee> specification =
                EmployeeReportSpecification.filter(
                        search,
                        departmentId,
                        designation,
                        fromDate,
                        toDate,
                        active
                );

        return employeeRepository
                .findAll(specification)
                .stream()
                .map(this::convertEmployeeToReportDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeReportSummaryDto getEmployeeReportSummary() {

        User loggedInUser = getLoggedInUser();

        List<Employee> employees;

        if (isManager(loggedInUser)) {

            Long managerDepartmentId =
                    getManagerDepartmentId(loggedInUser);

            if (managerDepartmentId == null) {
                return new EmployeeReportSummaryDto(
                        0L,
                        0L,
                        0L,
                        0.0
                );
            }

            Specification<Employee> specification =
                    EmployeeReportSpecification.filter(
                            null,
                            managerDepartmentId,
                            null,
                            null,
                            null,
                            null
                    );

            employees =
                    employeeRepository.findAll(
                            specification
                    );

        } else {

            employees =
                    employeeRepository.findAll();
        }

        Long totalEmployees =
                (long) employees.size();

        Long activeEmployees =
                employees.stream()
                        .filter(employee ->
                                employee.getUser() != null &&
                                        employee.getUser().isActive())
                        .count();

        Long inactiveEmployees =
                totalEmployees - activeEmployees;

        Double averageSalary =
                employees.stream()
                        .map(Employee::getSalary)
                        .filter(salary -> salary != null)
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);

        return new EmployeeReportSummaryDto(
                totalEmployees,
                activeEmployees,
                inactiveEmployees,
                averageSalary
        );
    }

    @Override
    public List<DepartmentReportDto> getDepartmentReport() {

        return departmentRepository.findAll()
                .stream()
                .map(department -> {

                    User manager =
                            department.getManager();

                    Long employeeCount =
                            employeeRepository.findAll()
                                    .stream()
                                    .filter(employee ->
                                            employee.getDepartment() != null &&
                                                    employee.getDepartment()
                                                            .getId()
                                                            .equals(department.getId()))
                                    .count();

                    return new DepartmentReportDto(
                            department.getId(),
                            department.getDepartmentName(),
                            department.getDepartmentCode(),
                            department.getDescription(),
                            manager != null
                                    ? manager.getId()
                                    : null,
                            getUserName(manager),
                            employeeCount
                    );
                })
                .toList();
    }

    @Override
    public List<ProjectReportDto> getProjectReport() {

        return projectRepository.findAll()
                .stream()
                .map(project -> {

                    Long employeeCount =
                            project.getEmployees() != null
                                    ? (long) project.getEmployees().size()
                                    : 0L;

                    Long taskCount =
                            taskRepository.countByProjectId(
                                    project.getId()
                            );

                    return new ProjectReportDto(
                            project.getId(),
                            project.getProjectName(),
                            project.getDescription(),
                            project.getStartDate(),
                            project.getEndDate(),
                            project.getStatus(),
                            employeeCount,
                            taskCount
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskReportDto> getTaskReport() {

        return taskRepository.findAll()
                .stream()
                .flatMap(task -> {

                    Project project =
                            task.getProject();

                    List<Employee> employees =
                            task.getEmployees();

                    if (employees == null ||
                            employees.isEmpty()) {

                        return java.util.stream.Stream.of(
                                createTaskReportDto(
                                        task,
                                        project,
                                        null
                                )
                        );
                    }

                    return employees.stream()
                            .map(employee ->
                                    createTaskReportDto(
                                            task,
                                            project,
                                            employee
                                    )
                            );
                })
                .toList();
    }

    private TaskReportDto createTaskReportDto(
            Task task,
            Project project,
            Employee employee) {

        return new TaskReportDto(
                task.getId(),
                task.getTaskTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),

                project != null
                        ? project.getId()
                        : null,

                project != null
                        ? project.getProjectName()
                        : null,

                employee != null
                        ? employee.getId()
                        : null,

                employee != null
                        ? employee.getEmployeeCode()
                        : null,

                employee != null
                        ? getEmployeeName(employee)
                        : null
        );
    }

    @Override
    public List<AttendanceReportDto> getAttendanceReport() {

        return attendanceRepository.findAll()
                .stream()
                .map(attendance -> {

                    Employee employee =
                            attendance.getEmployee();

                    return new AttendanceReportDto(
                            attendance.getId(),
                            attendance.getAttendanceDate(),
                            attendance.getCheckInTime(),
                            attendance.getCheckOutTime(),
                            attendance.getBreakStartTime(),
                            attendance.getBreakEndTime(),
                            attendance.getBreakDuration(),
                            attendance.getWorkingMinutes(),
                            attendance.getActualWorkingMinutes(),
                            attendance.getOvertimeMinutes(),
                            attendance.getStatus(),
                            employee != null
                                    ? employee.getId()
                                    : null,
                            employee != null
                                    ? employee.getEmployeeCode()
                                    : null,
                            employee != null
                                    ? getEmployeeName(employee)
                                    : null
                    );
                })
                .toList();
    }

    @Override
    public List<LeaveReportDto> getLeaveReport() {

        return leaveRequestRepository.findAll()
                .stream()
                .map(leaveRequest -> {

                    Employee employee =
                            leaveRequest.getEmployee();

                    Long totalLeaveDays = 0L;

                    if (leaveRequest.getStartDate() != null &&
                            leaveRequest.getEndDate() != null) {

                        totalLeaveDays =
                                ChronoUnit.DAYS.between(
                                        leaveRequest.getStartDate(),
                                        leaveRequest.getEndDate()
                                ) + 1;
                    }

                    return new LeaveReportDto(
                            leaveRequest.getId(),
                            leaveRequest.getStartDate(),
                            leaveRequest.getEndDate(),
                            leaveRequest.getLeaveType(),
                            leaveRequest.getStatus(),
                            leaveRequest.getReason(),
                            totalLeaveDays,
                            employee != null
                                    ? employee.getId()
                                    : null,
                            employee != null
                                    ? employee.getEmployeeCode()
                                    : null,
                            employee != null
                                    ? getEmployeeName(employee)
                                    : null
                    );
                })
                .toList();
    }

    @Override
    public List<PayrollReportDto> getPayrollReport() {

        return payrollRepository.findAll()
                .stream()
                .map(payroll -> {

                    Employee employee =
                            payroll.getEmployee();

                    return new PayrollReportDto(
                            payroll.getId(),
                            payroll.getPayrollMonth(),
                            payroll.getPayrollYear(),
                            payroll.getBasicSalary(),
                            payroll.getBonus(),
                            payroll.getAllowances(),
                            payroll.getGrossSalary(),
                            payroll.getDeductions(),
                            payroll.getNetSalary(),
                            payroll.getPaymentDate(),
                            payroll.getStatus(),
                            employee != null
                                    ? employee.getId()
                                    : null,
                            employee != null
                                    ? employee.getEmployeeCode()
                                    : null,
                            employee != null
                                    ? getEmployeeName(employee)
                                    : null
                    );
                })
                .toList();
    }

    @Override
    public List<PerformanceReportDto> getPerformanceReport() {

        return performanceReviewRepository.findAll()
                .stream()
                .map(review -> {

                    Employee employee =
                            review.getEmployee();

                    User reviewer =
                            review.getReviewer();

                    return new PerformanceReportDto(
                            review.getId(),
                            review.getReviewDate(),
                            review.getRating(),
                            review.getReviewPeriod(),
                            review.getComments(),
                            review.getStatus(),
                            employee != null
                                    ? employee.getId()
                                    : null,
                            employee != null
                                    ? employee.getEmployeeCode()
                                    : null,
                            employee != null
                                    ? getEmployeeName(employee)
                                    : null,
                            reviewer != null
                                    ? reviewer.getId()
                                    : null,
                            getUserName(reviewer),
                            review.isEmployeeAcknowledged(),
                            review.getAcknowledgedAt()
                    );
                })
                .toList();
    }

    @Override
    public List<DocumentReportDto> getDocumentReport() {

        return employeeDocumentRepository.findAll()
                .stream()
                .map(document -> {

                    Employee employee =
                            document.getEmployee();

                    return new DocumentReportDto(
                            document.getId(),
                            document.getDocumentName(),
                            document.getDocumentType(),
                            document.getFileName(),
                            document.getFileType(),
                            document.getFilePath(),
                            employee != null
                                    ? employee.getId()
                                    : null,
                            employee != null
                                    ? employee.getEmployeeCode()
                                    : null,
                            employee != null
                                    ? getEmployeeName(employee)
                                    : null
                    );
                })
                .toList();
    }

    private EmployeeReportDto convertEmployeeToReportDto(
            Employee employee) {

        User user =
                employee.getUser();

        Department department =
                employee.getDepartment();

        String employeeName =
                getUserName(user);

        String email =
                user != null
                        ? user.getEmail()
                        : null;

        String departmentName =
                department != null
                        ? department.getDepartmentName()
                        : null;

        boolean active =
                user != null &&
                        user.isActive();

        List<String> projects =
                employee.getProjects() != null
                        ? employee.getProjects()
                        .stream()
                        .map(Project::getProjectName)
                        .toList()
                        : List.of();

        List<String> assignedTasks =
                taskRepository
                        .findByEmployeesId(
                                employee.getId()
                        )
                        .stream()
                        .map(Task::getTaskTitle)
                        .toList();

        return new EmployeeReportDto(
                employee.getId(),
                employee.getEmployeeCode(),
                employeeName,
                email,
                employee.getPhoneNumber(),
                departmentName,
                employee.getDesignation(),
                projects,
                assignedTasks,
                employee.getSalary(),
                employee.getJoiningDate(),
                active
        );
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().isBlank()) {

            return null;
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElse(null);
    }

    private boolean isManager(User user) {

        return user != null &&
                user.getRole() != null &&
                user.getRole().getRoleName() == RoleType.MANAGER;
    }

    private Long getManagerDepartmentId(User manager) {

        if (manager == null ||
                manager.getId() == null) {

            return null;
        }

        return departmentRepository
                .findAll()
                .stream()
                .filter(department ->
                        department.getManager() != null &&
                                department.getManager()
                                        .getId()
                                        .equals(manager.getId()))
                .map(Department::getId)
                .findFirst()
                .orElse(null);
    }

    private String getUserName(User user) {

        if (user == null) {
            return null;
        }

        String firstName =
                user.getFirstName() != null
                        ? user.getFirstName()
                        : "";

        String lastName =
                user.getLastName() != null
                        ? user.getLastName()
                        : "";

        String fullName =
                (firstName + " " + lastName).trim();

        return fullName.isEmpty()
                ? null
                : fullName;
    }

    private String getEmployeeName(Employee employee) {

        if (employee == null ||
                employee.getUser() == null) {

            return null;
        }

        return getUserName(
                employee.getUser()
        );
    }
}