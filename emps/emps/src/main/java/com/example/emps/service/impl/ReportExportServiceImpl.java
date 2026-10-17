package com.example.emps.service.impl;

import com.example.emps.dto.report.EmployeeReportDto;
import com.example.emps.dto.report.EmployeeReportSummaryDto;
import com.example.emps.entity.Department;
import com.example.emps.entity.Employee;
import com.example.emps.entity.Project;
import com.example.emps.entity.User;
import com.example.emps.enums.RoleType;
import com.example.emps.repository.DepartmentRepository;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.TaskRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.ReportExportService;
import com.example.emps.specification.EmployeeReportSpecification;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportExportServiceImpl implements ReportExportService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public ReportExportServiceImpl(
            EmployeeRepository employeeRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository) {

        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportEmployeesToPdf(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active) {

        List<EmployeeReportDto> employees =
                getFilteredEmployees(
                        search,
                        departmentId,
                        designation,
                        fromDate,
                        toDate,
                        active
                );

        EmployeeReportSummaryDto summary =
                createSummary(employees);

        try (ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {

            Document document =
                    new Document(
                            PageSize.A4.rotate(),
                            20,
                            20,
                            20,
                            20
                    );

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            Font titleFont =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD
                    );

            Font normalFont =
                    new Font(
                            Font.HELVETICA,
                            9,
                            Font.NORMAL
                    );

            Font headerFont =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.BOLD
                    );

            Paragraph title =
                    new Paragraph(
                            "Employee Report",
                            titleFont
                    );

            title.setAlignment(
                    Paragraph.ALIGN_CENTER
            );

            document.add(title);

            document.add(
                    new Paragraph(
                            "Total Employees: "
                                    + summary.getTotalEmployees()
                                    + "    Active: "
                                    + summary.getActiveEmployees()
                                    + "    Inactive: "
                                    + summary.getInactiveEmployees()
                                    + "    Average Salary: "
                                    + formatSalary(
                                    summary.getAverageSalary()
                            ),
                            normalFont
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            String[] headers = {
                    "ID",
                    "Code",
                    "Name",
                    "Email",
                    "Phone",
                    "Department",
                    "Designation",
                    "Projects",
                    "Assigned Tasks",
                    "Salary",
                    "Joining Date",
                    "Status"
            };

            PdfPTable table =
                    new PdfPTable(headers.length);

            table.setWidthPercentage(100);

            table.setWidths(
                    new float[]{
                            4,
                            6,
                            9,
                            12,
                            9,
                            8,
                            9,
                            12,
                            14,
                            8,
                            8,
                            6
                    }
            );

            for (String header : headers) {

                PdfPCell cell =
                        new PdfPCell(
                                new Phrase(
                                        header,
                                        headerFont
                                )
                        );

                cell.setHorizontalAlignment(
                        PdfPCell.ALIGN_CENTER
                );

                cell.setVerticalAlignment(
                        PdfPCell.ALIGN_MIDDLE
                );

                cell.setPadding(5);

                table.addCell(cell);
            }

            for (EmployeeReportDto employee : employees) {

                addPdfCell(
                        table,
                        String.valueOf(
                                employee.getEmployeeId()
                        ),
                        normalFont
                );

                addPdfCell(
                        table,
                        employee.getEmployeeCode(),
                        normalFont
                );

                addPdfCell(
                        table,
                        employee.getEmployeeName(),
                        normalFont
                );

                addPdfCell(
                        table,
                        employee.getEmail(),
                        normalFont
                );

                addPdfCell(
                        table,
                        employee.getPhoneNumber(),
                        normalFont
                );

                addPdfCell(
                        table,
                        employee.getDepartmentName(),
                        normalFont
                );

                addPdfCell(
                        table,
                        employee.getDesignation(),
                        normalFont
                );

                addPdfCell(
                        table,
                        formatList(
                                employee.getProjects()
                        ),
                        normalFont
                );

                addPdfCell(
                        table,
                        formatList(
                                employee.getAssignedTasks()
                        ),
                        normalFont
                );

                addPdfCell(
                        table,
                        formatSalary(
                                employee.getSalary()
                        ),
                        normalFont
                );

                addPdfCell(
                        table,
                        formatDate(
                                employee.getJoiningDate()
                        ),
                        normalFont
                );

                addPdfCell(
                        table,
                        employee.isActive()
                                ? "Active"
                                : "Inactive",
                        normalFont
                );
            }

            document.add(table);

            document.close();

            return outputStream.toByteArray();

        } catch (
                DocumentException |
                IOException exception
        ) {

            throw new IllegalStateException(
                    "Failed to generate employee PDF report",
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportEmployeesToExcel(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active) {

        List<EmployeeReportDto> employees =
                getFilteredEmployees(
                        search,
                        departmentId,
                        designation,
                        fromDate,
                        toDate,
                        active
                );

        try (
                Workbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet(
                            "Employee Report"
                    );

            String[] headers = {
                    "Employee ID",
                    "Employee Code",
                    "Employee Name",
                    "Email",
                    "Phone Number",
                    "Department",
                    "Designation",
                    "Projects",
                    "Assigned Tasks",
                    "Salary",
                    "Joining Date",
                    "Status"
            };

            Row headerRow =
                    sheet.createRow(0);

            CellStyle headerStyle =
                    workbook.createCellStyle();

            org.apache.poi.ss.usermodel.Font headerFont =
                    workbook.createFont();

            headerFont.setBold(true);

            headerStyle.setFont(headerFont);

            for (
                    int i = 0;
                    i < headers.length;
                    i++
            ) {

                Cell cell =
                        headerRow.createCell(i);

                cell.setCellValue(
                        headers[i]
                );

                cell.setCellStyle(
                        headerStyle
                );
            }

            int rowIndex = 1;

            for (
                    EmployeeReportDto employee :
                    employees
            ) {

                Row row =
                        sheet.createRow(
                                rowIndex++
                        );

                int column = 0;

                row.createCell(column++)
                        .setCellValue(
                                employee.getEmployeeId() != null
                                        ? employee.getEmployeeId()
                                        : 0
                        );

                row.createCell(column++)
                        .setCellValue(
                                safe(
                                        employee.getEmployeeCode()
                                )
                        );

                row.createCell(column++)
                        .setCellValue(
                                safe(
                                        employee.getEmployeeName()
                                )
                        );

                row.createCell(column++)
                        .setCellValue(
                                safe(
                                        employee.getEmail()
                                )
                        );

                row.createCell(column++)
                        .setCellValue(
                                safe(
                                        employee.getPhoneNumber()
                                )
                        );

                row.createCell(column++)
                        .setCellValue(
                                safe(
                                        employee.getDepartmentName()
                                )
                        );

                row.createCell(column++)
                        .setCellValue(
                                safe(
                                        employee.getDesignation()
                                )
                        );

                row.createCell(column++)
                        .setCellValue(
                                formatList(
                                        employee.getProjects()
                                )
                        );

                row.createCell(column++)
                        .setCellValue(
                                formatList(
                                        employee.getAssignedTasks()
                                )
                        );

                row.createCell(column++)
                        .setCellValue(
                                employee.getSalary() != null
                                        ? employee.getSalary()
                                        : 0.0
                        );

                row.createCell(column++)
                        .setCellValue(
                                formatDate(
                                        employee.getJoiningDate()
                                )
                        );

                row.createCell(column)
                        .setCellValue(
                                employee.isActive()
                                        ? "Active"
                                        : "Inactive"
                        );
            }

            for (
                    int i = 0;
                    i < headers.length;
                    i++
            ) {

                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Failed to generate employee Excel report",
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportEmployeesToCsv(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active) {

        List<EmployeeReportDto> employees =
                getFilteredEmployees(
                        search,
                        departmentId,
                        designation,
                        fromDate,
                        toDate,
                        active
                );

        try (
                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream();

                CSVPrinter csvPrinter =
                        new CSVPrinter(
                                new java.io.OutputStreamWriter(
                                        outputStream,
                                        java.nio.charset.StandardCharsets.UTF_8
                                ),
                                CSVFormat.DEFAULT
                                        .builder()
                                        .setHeader(
                                                "Employee ID",
                                                "Employee Code",
                                                "Employee Name",
                                                "Email",
                                                "Phone Number",
                                                "Department",
                                                "Designation",
                                                "Projects",
                                                "Assigned Tasks",
                                                "Salary",
                                                "Joining Date",
                                                "Status"
                                        )
                                        .build()
                        )
        ) {

            for (
                    EmployeeReportDto employee :
                    employees
            ) {

                csvPrinter.printRecord(
                        employee.getEmployeeId(),
                        safe(
                                employee.getEmployeeCode()
                        ),
                        safe(
                                employee.getEmployeeName()
                        ),
                        safe(
                                employee.getEmail()
                        ),
                        safe(
                                employee.getPhoneNumber()
                        ),
                        safe(
                                employee.getDepartmentName()
                        ),
                        safe(
                                employee.getDesignation()
                        ),
                        formatList(
                                employee.getProjects()
                        ),
                        formatList(
                                employee.getAssignedTasks()
                        ),
                        employee.getSalary(),
                        formatDate(
                                employee.getJoiningDate()
                        ),
                        employee.isActive()
                                ? "Active"
                                : "Inactive"
                );
            }

            csvPrinter.flush();

            return outputStream.toByteArray();

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Failed to generate employee CSV report",
                    exception
            );
        }
    }

    private List<EmployeeReportDto> getFilteredEmployees(
            String search,
            Long departmentId,
            String designation,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active) {

        User loggedInUser =
                getLoggedInUser();

        if (isManager(loggedInUser)) {

            Long managerDepartmentId =
                    getManagerDepartmentId(
                            loggedInUser
                    );

            if (managerDepartmentId == null) {
                return List.of();
            }

            departmentId =
                    managerDepartmentId;
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
                .map(this::convertToDto)
                .toList();
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
                .findByEmail(
                        authentication.getName()
                )
                .orElse(null);
    }

    private boolean isManager(User user) {

        return user != null &&
                user.getRole() != null &&
                user.getRole().getRoleName()
                        == RoleType.MANAGER;
    }

    private Long getManagerDepartmentId(
            User manager) {

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
                                        .equals(
                                                manager.getId()
                                        ))
                .map(Department::getId)
                .findFirst()
                .orElse(null);
    }

    private EmployeeReportDto convertToDto(
            Employee employee) {

        String employeeName =
                employee.getUser() != null
                        ? getUserName(
                        employee.getUser()
                )
                        : null;

        String email =
                employee.getUser() != null
                        ? employee.getUser().getEmail()
                        : null;

        String departmentName =
                employee.getDepartment() != null
                        ? employee.getDepartment()
                        .getDepartmentName()
                        : null;

        boolean active =
                employee.getUser() != null
                        && employee.getUser().isActive();

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
                        .map(task ->
                                task.getTaskTitle()
                        )
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

    private EmployeeReportSummaryDto createSummary(
            List<EmployeeReportDto> employees) {

        long totalEmployees =
                employees.size();

        long activeEmployees =
                employees.stream()
                        .filter(
                                EmployeeReportDto::isActive
                        )
                        .count();

        long inactiveEmployees =
                totalEmployees -
                        activeEmployees;

        double averageSalary =
                employees.stream()
                        .map(
                                EmployeeReportDto::getSalary
                        )
                        .filter(
                                salary -> salary != null
                        )
                        .mapToDouble(
                                Double::doubleValue
                        )
                        .average()
                        .orElse(0.0);

        return new EmployeeReportSummaryDto(
                totalEmployees,
                activeEmployees,
                inactiveEmployees,
                averageSalary
        );
    }

    private void addPdfCell(
            PdfPTable table,
            String value,
            Font font) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                safe(value),
                                font
                        )
                );

        cell.setVerticalAlignment(
                PdfPCell.ALIGN_MIDDLE
        );

        cell.setPadding(4);

        table.addCell(cell);
    }

    private String formatList(
            List<String> values) {

        if (
                values == null ||
                        values.isEmpty()
        ) {
            return "-";
        }

        return String.join(
                ", ",
                values
        );
    }

    private String formatDate(
            LocalDate date) {

        return date != null
                ? date.toString()
                : "-";
    }

    private String formatSalary(
            Double salary) {

        return salary != null
                ? String.format(
                "%.2f",
                salary
        )
                : "0.00";
    }

    private String safe(
            String value) {

        return value != null
                ? value
                : "";
    }

    private String getUserName(
            User user) {

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
                (
                        firstName +
                                " " +
                                lastName
                ).trim();

        return fullName.isEmpty()
                ? null
                : fullName;
    }
}