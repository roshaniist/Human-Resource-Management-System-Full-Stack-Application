package com.example.emps.service.impl;

import com.example.emps.dto.PayrollDto;
import com.example.emps.entity.Employee;
import com.example.emps.entity.Payroll;
import com.example.emps.entity.User;
import com.example.emps.enums.NotificationType;
import com.example.emps.enums.PayrollStatus;
import com.example.emps.exception.BadRequestException;
import com.example.emps.exception.ResourceNotFoundException;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.PayrollRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.NotificationService;
import com.example.emps.service.PayrollService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PayrollServiceImpl(
            PayrollRepository payrollRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public PayrollDto generatePayroll(PayrollDto payrollDto) {

        validatePayrollPeriod(
                payrollDto.getPayrollMonth(),
                payrollDto.getPayrollYear()
        );

        Employee employee =
                getEmployee(payrollDto.getEmployeeId());

        if (payrollRepository
                .existsByEmployeeIdAndPayrollMonthAndPayrollYear(
                        employee.getId(),
                        payrollDto.getPayrollMonth(),
                        payrollDto.getPayrollYear())) {

            throw new BadRequestException(
                    "Payroll already exists for this employee for the selected month and year"
            );
        }

        validateSalaryValues(payrollDto);

        Payroll payroll = new Payroll();

        payroll.setEmployee(employee);

        payroll.setPayrollMonth(
                payrollDto.getPayrollMonth()
        );

        payroll.setPayrollYear(
                payrollDto.getPayrollYear()
        );

        payroll.setBasicSalary(
                payrollDto.getBasicSalary()
        );

        payroll.setBonus(
                defaultValue(payrollDto.getBonus())
        );

        payroll.setAllowances(
                defaultValue(payrollDto.getAllowances())
        );

        payroll.setDeductions(
                defaultValue(payrollDto.getDeductions())
        );

        calculateSalary(payroll);

        payroll.setStatus(PayrollStatus.DRAFT);

        payroll.setPaymentDate(null);

        Payroll savedPayroll =
                payrollRepository.save(payroll);

        notifyEmployee(
                savedPayroll,
                "Payroll Generated",
                "Your payroll for "
                        + getPayrollPeriod(savedPayroll)
                        + " has been generated."
        );

        return convertToDto(savedPayroll);
    }

    @Override
    public PayrollDto updatePayroll(
            Long id,
            PayrollDto payrollDto) {

        Payroll existingPayroll =
                payrollRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payroll record not found with id: "
                                                + id
                                )
                        );

        if (existingPayroll.getStatus()
                != PayrollStatus.DRAFT) {

            throw new BadRequestException(
                    "Only DRAFT payroll can be modified"
            );
        }

        validatePayrollPeriod(
                payrollDto.getPayrollMonth(),
                payrollDto.getPayrollYear()
        );

        Employee employee =
                getEmployee(payrollDto.getEmployeeId());

        payrollRepository
                .findByEmployeeIdAndPayrollMonthAndPayrollYear(
                        employee.getId(),
                        payrollDto.getPayrollMonth(),
                        payrollDto.getPayrollYear()
                )
                .ifPresent(payroll -> {

                    if (!payroll.getId()
                            .equals(existingPayroll.getId())) {

                        throw new BadRequestException(
                                "Another payroll already exists for this employee for the selected month and year"
                        );
                    }
                });

        validateSalaryValues(payrollDto);

        existingPayroll.setEmployee(employee);

        existingPayroll.setPayrollMonth(
                payrollDto.getPayrollMonth()
        );

        existingPayroll.setPayrollYear(
                payrollDto.getPayrollYear()
        );

        existingPayroll.setBasicSalary(
                payrollDto.getBasicSalary()
        );

        existingPayroll.setBonus(
                defaultValue(payrollDto.getBonus())
        );

        existingPayroll.setAllowances(
                defaultValue(payrollDto.getAllowances())
        );

        existingPayroll.setDeductions(
                defaultValue(payrollDto.getDeductions())
        );

        calculateSalary(existingPayroll);

        existingPayroll.setStatus(
                PayrollStatus.DRAFT
        );

        existingPayroll.setPaymentDate(null);

        Payroll updatedPayroll =
                payrollRepository.save(existingPayroll);

        notifyEmployee(
                updatedPayroll,
                "Payroll Updated",
                "Your payroll for "
                        + getPayrollPeriod(updatedPayroll)
                        + " has been updated."
        );

        return convertToDto(updatedPayroll);
    }

    @Override
    public void processPayroll(Long id) {

        Payroll payroll =
                payrollRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payroll record not found with id: "
                                                + id
                                )
                        );

        if (payroll.getStatus()
                != PayrollStatus.DRAFT) {

            throw new BadRequestException(
                    "Only DRAFT payroll can be processed"
            );
        }

        payroll.setStatus(
                PayrollStatus.PROCESSED
        );

        payroll.setPaymentDate(null);

        payrollRepository.save(payroll);

        notifyEmployee(
                payroll,
                "Payroll Processed",
                "Your payroll for "
                        + getPayrollPeriod(payroll)
                        + " has been processed."
        );
    }

    @Override
    public void markPayrollAsPaid(Long id) {

        Payroll payroll =
                payrollRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payroll record not found with id: "
                                                + id
                                )
                        );

        if (payroll.getStatus()
                != PayrollStatus.PROCESSED) {

            throw new BadRequestException(
                    "Only PROCESSED payroll can be marked as PAID"
            );
        }

        payroll.setStatus(
                PayrollStatus.PAID
        );

        payroll.setPaymentDate(
                LocalDate.now()
        );

        payrollRepository.save(payroll);

        notifyEmployee(
                payroll,
                "Salary Paid",
                "Your salary for "
                        + getPayrollPeriod(payroll)
                        + " has been marked as paid."
        );
    }

    @Override
    public void cancelPayroll(Long id) {

        Payroll payroll =
                payrollRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payroll record not found with id: "
                                                + id
                                )
                        );

        if (payroll.getStatus()
                == PayrollStatus.PAID) {

            throw new BadRequestException(
                    "Paid payroll cannot be cancelled"
            );
        }

        if (payroll.getStatus()
                == PayrollStatus.CANCELLED) {

            throw new BadRequestException(
                    "Payroll is already cancelled"
            );
        }

        payroll.setStatus(
                PayrollStatus.CANCELLED
        );

        payroll.setPaymentDate(null);

        payrollRepository.save(payroll);

        notifyEmployee(
                payroll,
                "Payroll Cancelled",
                "Your payroll for "
                        + getPayrollPeriod(payroll)
                        + " has been cancelled."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollDto getPayrollById(Long id) {

        Payroll payroll =
                payrollRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payroll record not found with id: "
                                                + id
                                )
                        );

        if (isEmployee()) {

            checkEmployeeAccess(
                    payroll.getEmployee()
            );
        }

        return convertToDto(payroll);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollDto> getAllPayrolls() {

        return payrollRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollDto> getMyPayrolls() {

        Employee employee =
                getLoggedInEmployee();

        return payrollRepository
                .findByEmployeeIdOrderByPayrollYearDescPayrollMonthDesc(
                        employee.getId()
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollDto> getEmployeePayrollHistory(
            Long employeeId) {

        getEmployee(employeeId);

        return payrollRepository
                .findByEmployeeIdOrderByPayrollYearDescPayrollMonthDesc(
                        employeeId
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollDto> getPayrollByPeriod(
            Integer payrollMonth,
            Integer payrollYear) {

        validatePayrollPeriod(
                payrollMonth,
                payrollYear
        );

        return payrollRepository
                .findByPayrollMonthAndPayrollYear(
                        payrollMonth,
                        payrollYear
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void deletePayroll(Long id) {

        Payroll payroll =
                payrollRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payroll record not found with id: "
                                                + id
                                )
                        );

        if (payroll.getStatus()
                == PayrollStatus.PAID) {

            throw new BadRequestException(
                    "Paid payroll cannot be deleted"
            );
        }

        Employee employee =
                payroll.getEmployee();

        String payrollPeriod =
                getPayrollPeriod(payroll);

        Long payrollId =
                payroll.getId();

        payrollRepository.delete(payroll);

        notifyEmployee(
                employee,
                payrollId,
                "Payroll Deleted",
                "Your payroll for "
                        + payrollPeriod
                        + " has been deleted."
        );
    }

    private void notifyEmployee(
            Payroll payroll,
            String title,
            String message) {

        if (payroll == null) {
            return;
        }

        notifyEmployee(
                payroll.getEmployee(),
                payroll.getId(),
                title,
                message
        );
    }

    private void notifyEmployee(
            Employee employee,
            Long payrollId,
            String title,
            String message) {

        if (employee == null ||
                employee.getUser() == null ||
                employee.getUser().getId() == null) {

            return;
        }

        notificationService.createNotification(
                employee.getUser().getId(),
                title,
                message,
                NotificationType.PAYROLL,
                "PAYROLL",
                payrollId
        );
    }

    private String getPayrollPeriod(
            Payroll payroll) {

        if (payroll == null) {
            return "";
        }

        return String.format(
                "%02d/%d",
                payroll.getPayrollMonth(),
                payroll.getPayrollYear()
        );
    }

    private void calculateSalary(Payroll payroll) {

        BigDecimal basicSalary =
                defaultValue(
                        payroll.getBasicSalary()
                );

        BigDecimal bonus =
                defaultValue(
                        payroll.getBonus()
                );

        BigDecimal allowances =
                defaultValue(
                        payroll.getAllowances()
                );

        BigDecimal deductions =
                defaultValue(
                        payroll.getDeductions()
                );

        BigDecimal grossSalary =
                basicSalary
                        .add(bonus)
                        .add(allowances);

        BigDecimal netSalary =
                grossSalary.subtract(deductions);

        if (netSalary.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Deductions cannot be greater than gross salary"
            );
        }

        payroll.setGrossSalary(
                grossSalary
        );

        payroll.setNetSalary(
                netSalary
        );
    }

    private void validatePayrollPeriod(
            Integer month,
            Integer year) {

        if (month == null ||
                month < 1 ||
                month > 12) {

            throw new BadRequestException(
                    "Payroll month must be between 1 and 12"
            );
        }

        if (year == null ||
                year < 2000 ||
                year > 2100) {

            throw new BadRequestException(
                    "Invalid payroll year"
            );
        }
    }

    private void validateSalaryValues(
            PayrollDto payrollDto) {

        if (payrollDto.getBasicSalary() == null ||
                payrollDto.getBasicSalary()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Basic salary cannot be negative"
            );
        }

        if (payrollDto.getBonus() != null &&
                payrollDto.getBonus()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Bonus cannot be negative"
            );
        }

        if (payrollDto.getAllowances() != null &&
                payrollDto.getAllowances()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Allowances cannot be negative"
            );
        }

        if (payrollDto.getDeductions() != null &&
                payrollDto.getDeductions()
                        .compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    "Deductions cannot be negative"
            );
        }
    }

    private Employee getEmployee(Long employeeId) {

        if (employeeId == null) {

            throw new BadRequestException(
                    "Employee ID is required"
            );
        }

        return employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id: "
                                        + employeeId
                        )
                );
    }

    private Employee getLoggedInEmployee() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new BadRequestException(
                    "User is not authenticated"
            );
        }

        String email =
                authentication.getName();

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Logged-in user not found"
                                )
                        );

        return employeeRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee profile not found"
                        )
                );
    }

    private void checkEmployeeAccess(
            Employee payrollEmployee) {

        Employee loggedInEmployee =
                getLoggedInEmployee();

        if (payrollEmployee == null ||
                !payrollEmployee.getId()
                        .equals(loggedInEmployee.getId())) {

            throw new BadRequestException(
                    "You can access only your own payroll"
            );
        }
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
                                        .equals("ROLE_EMPLOYEE")
                        );
    }

    private BigDecimal defaultValue(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }

    private PayrollDto convertToDto(
            Payroll payroll) {

        PayrollDto dto =
                new PayrollDto();

        dto.setId(
                payroll.getId()
        );

        if (payroll.getEmployee() != null) {

            dto.setEmployeeId(
                    payroll.getEmployee().getId()
            );
        }

        dto.setPayrollMonth(
                payroll.getPayrollMonth()
        );

        dto.setPayrollYear(
                payroll.getPayrollYear()
        );

        dto.setBasicSalary(
                payroll.getBasicSalary()
        );

        dto.setBonus(
                payroll.getBonus()
        );

        dto.setAllowances(
                payroll.getAllowances()
        );

        dto.setGrossSalary(
                payroll.getGrossSalary()
        );

        dto.setDeductions(
                payroll.getDeductions()
        );

        dto.setNetSalary(
                payroll.getNetSalary()
        );

        dto.setPaymentDate(
                payroll.getPaymentDate()
        );

        dto.setStatus(
                payroll.getStatus()
        );

        return dto;
    }
}