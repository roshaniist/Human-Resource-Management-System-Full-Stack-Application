package com.example.emps.service.impl;

import com.example.emps.dto.DashboardDto;
import com.example.emps.entity.Attendance;
import com.example.emps.entity.Department;
import com.example.emps.entity.Employee;
import com.example.emps.entity.Payroll;
import com.example.emps.entity.PerformanceReview;
import com.example.emps.entity.Project;
import com.example.emps.entity.SupportTicket;
import com.example.emps.entity.Task;
import com.example.emps.entity.User;
import com.example.emps.enums.AttendanceStatus;
import com.example.emps.enums.LeaveStatus;
import com.example.emps.enums.PayrollStatus;
import com.example.emps.enums.PerformanceReviewStatus;
import com.example.emps.enums.ProjectStatus;
import com.example.emps.enums.RoleType;
import com.example.emps.enums.TaskStatus;
import com.example.emps.enums.TicketStatus;
import com.example.emps.repository.AttendanceRepository;
import com.example.emps.repository.DepartmentRepository;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.LeaveRequestRepository;
import com.example.emps.repository.NotificationRepository;
import com.example.emps.repository.PayrollRepository;
import com.example.emps.repository.PerformanceReviewRepository;
import com.example.emps.repository.ProjectRepository;
import com.example.emps.repository.SupportTicketRepository;
import com.example.emps.repository.TaskRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SupportTicketRepository supportTicketRepository;

    public DashboardServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            AttendanceRepository attendanceRepository,
            LeaveRequestRepository leaveRequestRepository,
            PayrollRepository payrollRepository,
            PerformanceReviewRepository performanceReviewRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            SupportTicketRepository supportTicketRepository) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.payrollRepository = payrollRepository;
        this.performanceReviewRepository = performanceReviewRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    public DashboardDto getDashboardData(String email) {

        DashboardDto dashboard = new DashboardDto();

        if (email == null || email.isBlank()) {
            return dashboard;
        }

        User currentUser = userRepository.findByEmail(email)
                .orElse(null);

        if (currentUser == null || currentUser.getRole() == null) {
            return dashboard;
        }

        LocalDate today = LocalDate.now();

        RoleType role = currentUser.getRole().getRoleName();

        if (role == RoleType.ADMIN || role == RoleType.HR) {

            loadOrganizationDashboard(
                    dashboard,
                    today
            );

        } else if (role == RoleType.MANAGER) {

            loadManagerDashboard(
                    dashboard,
                    currentUser,
                    today
            );

        } else if (role == RoleType.EMPLOYEE) {

            loadEmployeeDashboard(
                    dashboard,
                    currentUser,
                    today
            );

        } else if (role == RoleType.IT_SUPPORT) {

            loadITSupportDashboard(
                    dashboard,
                    currentUser
            );
        }

        dashboard.setUnreadNotifications(
                notificationRepository
                        .countByRecipientIdAndReadFalse(
                                currentUser.getId()
                        )
        );

        return dashboard;
    }

    private void loadOrganizationDashboard(
            DashboardDto dashboard,
            LocalDate today) {

        List<Employee> employees =
                employeeRepository.findAll();

        List<Project> projects =
                projectRepository.findAll();

        List<Task> tasks =
                taskRepository.findAll();

        dashboard.setTotalEmployees(
                employees.size()
        );

        dashboard.setTotalDepartments(
                departmentRepository.count()
        );

        dashboard.setTotalProjects(
                projects.size()
        );

        dashboard.setActiveProjects(
                projects.stream()
                        .filter(project ->
                                project != null
                                        && project.getStatus()
                                        == ProjectStatus.IN_PROGRESS)
                        .count()
        );

        loadTaskData(
                dashboard,
                tasks,
                today
        );

        loadOrganizationAttendance(
                dashboard,
                today
        );

        dashboard.setPendingLeaveRequests(
                leaveRequestRepository.countByStatus(
                        LeaveStatus.PENDING
                )
        );

        loadOrganizationPayroll(
                dashboard
        );

        loadOrganizationPerformance(
                dashboard
        );
    }

    private void loadManagerDashboard(
            DashboardDto dashboard,
            User manager,
            LocalDate today) {

        Department department =
                departmentRepository
                        .findByManagerId(manager.getId())
                        .orElse(null);

        if (department == null) {
            return;
        }

        List<Employee> teamEmployees =
                employeeRepository.findByDepartmentId(
                        department.getId()
                );

        Set<Long> employeeIds =
                teamEmployees.stream()
                        .filter(employee -> employee != null)
                        .map(Employee::getId)
                        .filter(id -> id != null)
                        .collect(Collectors.toSet());

        dashboard.setTotalEmployees(
                teamEmployees.size()
        );

        dashboard.setTotalDepartments(1);

        List<Project> teamProjects =
                projectRepository.findAll()
                        .stream()
                        .filter(project ->
                                isManagerProject(
                                        project,
                                        employeeIds
                                ))
                        .toList();

        dashboard.setTotalProjects(
                teamProjects.size()
        );

        dashboard.setActiveProjects(
                teamProjects.stream()
                        .filter(project ->
                                project != null
                                        && project.getStatus()
                                        == ProjectStatus.IN_PROGRESS)
                        .count()
        );

        List<Task> teamTasks =
                taskRepository.findAll()
                        .stream()
                        .filter(task ->
                                task != null
                                        && belongsToTeam(
                                        task.getEmployees(),
                                        employeeIds
                                ))
                        .toList();

        loadTaskData(
                dashboard,
                teamTasks,
                today
        );

        loadManagerAttendance(
                dashboard,
                teamEmployees,
                today
        );

        dashboard.setPendingLeaveRequests(
                leaveRequestRepository
                        .findByEmployeeDepartmentIdAndStatusOrderByStartDateDesc(
                                department.getId(),
                                LeaveStatus.PENDING
                        )
                        .size()
        );

        loadManagerPayroll(
                dashboard,
                employeeIds
        );

        loadManagerPerformance(
                dashboard,
                employeeIds
        );
    }

    private void loadEmployeeDashboard(
            DashboardDto dashboard,
            User user,
            LocalDate today) {

        Employee employee =
                employeeRepository
                        .findByUserId(user.getId())
                        .orElse(null);

        if (employee == null) {
            return;
        }

        Long employeeId = employee.getId();

        List<Project> employeeProjects =
                projectRepository.findByEmployees_Id(
                        employeeId
                );

        dashboard.setTotalProjects(
                employeeProjects.size()
        );

        dashboard.setActiveProjects(
                employeeProjects.stream()
                        .filter(project ->
                                project != null
                                        && project.getStatus()
                                        == ProjectStatus.IN_PROGRESS)
                        .count()
        );

        List<Task> employeeTasks =
                taskRepository.findByEmployeesId(
                        employeeId
                );

        loadTaskData(
                dashboard,
                employeeTasks,
                today
        );

        loadEmployeeAttendance(
                dashboard,
                employeeId,
                today
        );

        dashboard.setPendingLeaveRequests(
                leaveRequestRepository
                        .findByEmployeeIdAndStatusOrderByStartDateDesc(
                                employeeId,
                                LeaveStatus.PENDING
                        )
                        .size()
        );

        loadEmployeePayroll(
                dashboard,
                employeeId
        );

        loadEmployeePerformance(
                dashboard,
                employeeId
        );
    }

    private void loadITSupportDashboard(
            DashboardDto dashboard,
            User currentUser) {

        List<SupportTicket> tickets =
                supportTicketRepository
                        .findByAssignedToIdOrderByCreatedAtDesc(
                                currentUser.getId()
                        );

        if (tickets == null) {
            tickets = List.of();
        }

        long assignedTickets =
                tickets.size();

        long openTickets =
                tickets.stream()
                        .filter(ticket ->
                                ticket != null
                                        && (
                                        ticket.getStatus()
                                                == TicketStatus.OPEN
                                                || ticket.getStatus()
                                                == TicketStatus.ASSIGNED
                                )
                        )
                        .count();

        long inProgressTickets =
                tickets.stream()
                        .filter(ticket ->
                                ticket != null
                                        && (
                                        ticket.getStatus()
                                                == TicketStatus.IN_PROGRESS
                                                || ticket.getStatus()
                                                == TicketStatus.REOPENED
                                )
                        )
                        .count();

        long resolvedTickets =
                tickets.stream()
                        .filter(ticket ->
                                ticket != null
                                        && (
                                        ticket.getStatus()
                                                == TicketStatus.RESOLVED
                                                || ticket.getStatus()
                                                == TicketStatus.CLOSED
                                )
                        )
                        .count();

        dashboard.setAssignedTickets(
                assignedTickets
        );

        dashboard.setOpenTickets(
                openTickets
        );

        dashboard.setInProgressTickets(
                inProgressTickets
        );

        dashboard.setResolvedTickets(
                resolvedTickets
        );
    }

    private void loadTaskData(
            DashboardDto dashboard,
            List<Task> tasks,
            LocalDate today) {

        if (tasks == null) {
            tasks = List.of();
        }

        long totalTasks =
                tasks.size();

        long completedTasks =
                tasks.stream()
                        .filter(task ->
                                task != null
                                        && task.getStatus()
                                        == TaskStatus.COMPLETED)
                        .count();

        long overdueTasks =
                tasks.stream()
                        .filter(task ->
                                isOverdue(
                                        task,
                                        today
                                ))
                        .count();

        long pendingTasks =
                tasks.stream()
                        .filter(task ->
                                isPending(
                                        task,
                                        today
                                ))
                        .count();

        dashboard.setTotalTasks(
                totalTasks
        );

        dashboard.setCompletedTasks(
                completedTasks
        );

        dashboard.setPendingTasks(
                pendingTasks
        );

        dashboard.setOverdueTasks(
                overdueTasks
        );

        dashboard.setCompletedTaskPercentage(
                calculatePercentage(
                        completedTasks,
                        totalTasks
                )
        );

        dashboard.setPendingTaskPercentage(
                calculatePercentage(
                        pendingTasks,
                        totalTasks
                )
        );

        dashboard.setOverdueTaskPercentage(
                calculatePercentage(
                        overdueTasks,
                        totalTasks
                )
        );
    }

    private boolean isPending(
            Task task,
            LocalDate today) {

        if (task == null
                || task.getStatus() == null) {
            return false;
        }

        if (task.getStatus() == TaskStatus.COMPLETED) {
            return false;
        }

        if (isOverdue(task, today)) {
            return false;
        }

        return task.getStatus() == TaskStatus.TODO
                || task.getStatus() == TaskStatus.IN_PROGRESS
                || task.getStatus() == TaskStatus.ON_HOLD;
    }

    private void loadOrganizationAttendance(
            DashboardDto dashboard,
            LocalDate today) {

        dashboard.setTodayPresent(
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.PRESENT
                        )
        );

        dashboard.setTodayLate(
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.LATE
                        )
        );

        dashboard.setTodayHalfDay(
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.HALF_DAY
                        )
        );

        dashboard.setTodayAbsent(
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.ABSENT
                        )
        );

        dashboard.setTodayLeave(
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.LEAVE
                        )
        );
    }

    private void loadManagerAttendance(
            DashboardDto dashboard,
            List<Employee> employees,
            LocalDate today) {

        long present = 0;
        long late = 0;
        long halfDay = 0;
        long absent = 0;
        long leave = 0;

        for (Employee employee : employees) {

            if (employee == null
                    || employee.getId() == null) {
                continue;
            }

            Attendance attendance =
                    attendanceRepository
                            .findByEmployeeIdAndAttendanceDate(
                                    employee.getId(),
                                    today
                            )
                            .orElse(null);

            if (attendance == null
                    || attendance.getStatus() == null) {
                continue;
            }

            switch (attendance.getStatus()) {

                case PRESENT -> present++;

                case LATE -> late++;

                case HALF_DAY -> halfDay++;

                case ABSENT -> absent++;

                case LEAVE -> leave++;
            }
        }

        dashboard.setTodayPresent(present);
        dashboard.setTodayLate(late);
        dashboard.setTodayHalfDay(halfDay);
        dashboard.setTodayAbsent(absent);
        dashboard.setTodayLeave(leave);
    }

    private void loadEmployeeAttendance(
            DashboardDto dashboard,
            Long employeeId,
            LocalDate today) {

        Attendance attendance =
                attendanceRepository
                        .findByEmployeeIdAndAttendanceDate(
                                employeeId,
                                today
                        )
                        .orElse(null);

        if (attendance == null
                || attendance.getStatus() == null) {
            return;
        }

        switch (attendance.getStatus()) {

            case PRESENT ->
                    dashboard.setTodayPresent(1);

            case LATE ->
                    dashboard.setTodayLate(1);

            case HALF_DAY ->
                    dashboard.setTodayHalfDay(1);

            case ABSENT ->
                    dashboard.setTodayAbsent(1);

            case LEAVE ->
                    dashboard.setTodayLeave(1);
        }
    }

    private void loadOrganizationPayroll(
            DashboardDto dashboard) {

        dashboard.setDraftPayrolls(
                payrollRepository.countByStatus(
                        PayrollStatus.DRAFT
                )
        );

        dashboard.setProcessedPayrolls(
                payrollRepository.countByStatus(
                        PayrollStatus.PROCESSED
                )
        );

        dashboard.setPaidPayrolls(
                payrollRepository.countByStatus(
                        PayrollStatus.PAID
                )
        );

        dashboard.setCancelledPayrolls(
                payrollRepository.countByStatus(
                        PayrollStatus.CANCELLED
                )
        );

        BigDecimal totalPaidSalary =
                payrollRepository.sumNetSalaryByStatus(
                        PayrollStatus.PAID
                );

        dashboard.setTotalPaidSalary(
                totalPaidSalary != null
                        ? totalPaidSalary
                        : BigDecimal.ZERO
        );
    }

    private void loadManagerPayroll(
            DashboardDto dashboard,
            Set<Long> employeeIds) {

        List<Payroll> payrolls =
                payrollRepository.findAll()
                        .stream()
                        .filter(payroll ->
                                payroll != null
                                        && payroll.getEmployee() != null
                                        && payroll.getEmployee().getId() != null
                                        && employeeIds.contains(
                                        payroll.getEmployee().getId()
                                ))
                        .toList();

        loadPayrollData(
                dashboard,
                payrolls
        );
    }

    private void loadEmployeePayroll(
            DashboardDto dashboard,
            Long employeeId) {

        List<Payroll> payrolls =
                payrollRepository
                        .findByEmployeeIdOrderByPayrollYearDescPayrollMonthDesc(
                                employeeId
                        );

        loadPayrollData(
                dashboard,
                payrolls
        );
    }

    private void loadPayrollData(
            DashboardDto dashboard,
            List<Payroll> payrolls) {

        if (payrolls == null) {
            payrolls = List.of();
        }

        long draft =
                payrolls.stream()
                        .filter(payroll ->
                                payroll != null
                                        && payroll.getStatus()
                                        == PayrollStatus.DRAFT)
                        .count();

        long processed =
                payrolls.stream()
                        .filter(payroll ->
                                payroll != null
                                        && payroll.getStatus()
                                        == PayrollStatus.PROCESSED)
                        .count();

        long paid =
                payrolls.stream()
                        .filter(payroll ->
                                payroll != null
                                        && payroll.getStatus()
                                        == PayrollStatus.PAID)
                        .count();

        long cancelled =
                payrolls.stream()
                        .filter(payroll ->
                                payroll != null
                                        && payroll.getStatus()
                                        == PayrollStatus.CANCELLED)
                        .count();

        BigDecimal totalPaidSalary =
                payrolls.stream()
                        .filter(payroll ->
                                payroll != null
                                        && payroll.getStatus()
                                        == PayrollStatus.PAID)
                        .map(Payroll::getNetSalary)
                        .filter(salary -> salary != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        dashboard.setDraftPayrolls(draft);
        dashboard.setProcessedPayrolls(processed);
        dashboard.setPaidPayrolls(paid);
        dashboard.setCancelledPayrolls(cancelled);
        dashboard.setTotalPaidSalary(
                totalPaidSalary
        );
    }

    private void loadOrganizationPerformance(
            DashboardDto dashboard) {

        dashboard.setDraftReviews(
                performanceReviewRepository.countByStatus(
                        PerformanceReviewStatus.DRAFT
                )
        );

        dashboard.setSubmittedReviews(
                performanceReviewRepository.countByStatus(
                        PerformanceReviewStatus.SUBMITTED
                )
        );

        dashboard.setAcknowledgedReviews(
                performanceReviewRepository.countByStatus(
                        PerformanceReviewStatus.ACKNOWLEDGED
                )
        );

        dashboard.setCompletedReviews(
                performanceReviewRepository.countByStatus(
                        PerformanceReviewStatus.COMPLETED
                )
        );

        dashboard.setCancelledReviews(
                performanceReviewRepository.countByStatus(
                        PerformanceReviewStatus.CANCELLED
                )
        );
    }

    private void loadManagerPerformance(
            DashboardDto dashboard,
            Set<Long> employeeIds) {

        List<PerformanceReview> reviews =
                performanceReviewRepository.findAll()
                        .stream()
                        .filter(review ->
                                review != null
                                        && review.getEmployee() != null
                                        && review.getEmployee().getId() != null
                                        && employeeIds.contains(
                                        review.getEmployee().getId()
                                ))
                        .toList();

        loadPerformanceData(
                dashboard,
                reviews
        );
    }

    private void loadEmployeePerformance(
            DashboardDto dashboard,
            Long employeeId) {

        List<PerformanceReview> reviews =
                performanceReviewRepository.findByEmployeeId(
                        employeeId
                );

        loadPerformanceData(
                dashboard,
                reviews
        );
    }

    private void loadPerformanceData(
            DashboardDto dashboard,
            List<PerformanceReview> reviews) {

        if (reviews == null) {
            reviews = List.of();
        }

        dashboard.setDraftReviews(
                countReviews(
                        reviews,
                        PerformanceReviewStatus.DRAFT
                )
        );

        dashboard.setSubmittedReviews(
                countReviews(
                        reviews,
                        PerformanceReviewStatus.SUBMITTED
                )
        );

        dashboard.setAcknowledgedReviews(
                countReviews(
                        reviews,
                        PerformanceReviewStatus.ACKNOWLEDGED
                )
        );

        dashboard.setCompletedReviews(
                countReviews(
                        reviews,
                        PerformanceReviewStatus.COMPLETED
                )
        );

        dashboard.setCancelledReviews(
                countReviews(
                        reviews,
                        PerformanceReviewStatus.CANCELLED
                )
        );
    }

    private long countReviews(
            List<PerformanceReview> reviews,
            PerformanceReviewStatus status) {

        return reviews.stream()
                .filter(review ->
                        review != null
                                && review.getStatus() == status)
                .count();
    }

    private boolean isManagerProject(
            Project project,
            Set<Long> employeeIds) {

        if (project == null) {
            return false;
        }

        if (project.getManager() != null
                && project.getManager().getId() != null
                && employeeIds.contains(
                project.getManager().getId())) {

            return true;
        }

        return project.getEmployees() != null
                && project.getEmployees()
                .stream()
                .anyMatch(employee ->
                        employee != null
                                && employee.getId() != null
                                && employeeIds.contains(
                                employee.getId()
                        ));
    }

    private boolean belongsToTeam(
            List<Employee> employees,
            Set<Long> employeeIds) {

        if (employees == null
                || employees.isEmpty()) {
            return false;
        }

        return employees.stream()
                .anyMatch(employee ->
                        employee != null
                                && employee.getId() != null
                                && employeeIds.contains(
                                employee.getId()
                        ));
    }

    private boolean isOverdue(
            Task task,
            LocalDate today) {

        if (task == null
                || task.getDueDate() == null) {
            return false;
        }

        if (task.getStatus() == null
                || task.getStatus() == TaskStatus.COMPLETED) {
            return false;
        }

        return task.getDueDate()
                .isBefore(today);
    }

    private double calculatePercentage(
            long value,
            long total) {

        if (total <= 0) {
            return 0.0;
        }

        return Math.round(
                ((double) value / total) * 10000
        ) / 100.0;
    }
}