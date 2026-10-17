package com.example.emps.service.impl;

import com.example.emps.dto.EmployeeDocumentDto;
import com.example.emps.entity.Employee;
import com.example.emps.entity.EmployeeDocument;
import com.example.emps.entity.Project;
import com.example.emps.entity.User;
import com.example.emps.enums.DocumentType;
import com.example.emps.enums.NotificationType;
import com.example.emps.enums.RoleType;
import com.example.emps.repository.EmployeeDocumentRepository;
import com.example.emps.repository.EmployeeRepository;
import com.example.emps.repository.ProjectRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.EmployeeDocumentService;
import com.example.emps.service.NotificationService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class EmployeeDocumentServiceImpl
        implements EmployeeDocumentService {

    private final EmployeeDocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Value("${app.upload.documents-directory}")
    private String uploadDirectory;

    public EmployeeDocumentServiceImpl(
            EmployeeDocumentRepository documentRepository,
            EmployeeRepository employeeRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.documentRepository = documentRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public EmployeeDocumentDto uploadMyDocument(
            MultipartFile file,
            String documentName,
            String documentType) {

        User loggedInUser = getLoggedInUser();

        Employee employee =
                employeeRepository.findByUserId(
                        loggedInUser.getId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found"
                        )
                );

        return saveDocument(
                file,
                documentName,
                documentType,
                employee,
                loggedInUser,
                true
        );
    }

    @Override
    public EmployeeDocumentDto uploadDocumentForEmployee(
            MultipartFile file,
            String documentName,
            String documentType,
            Long employeeId) {

        if (!isAdmin() && !isHr()) {
            throw new RuntimeException(
                    "Only Admin or HR can upload document for another employee"
            );
        }

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

        User loggedInUser = getLoggedInUser();

        return saveDocument(
                file,
                documentName,
                documentType,
                employee,
                loggedInUser,
                false
        );
    }

    private EmployeeDocumentDto saveDocument(
            MultipartFile file,
            String documentName,
            String documentType,
            Employee employee,
            User uploadedBy,
            boolean notifyHrAndAdmin) {

        if (documentName == null ||
                documentName.trim().isEmpty()) {

            throw new RuntimeException(
                    "Document name is required"
            );
        }

        if (documentType == null ||
                documentType.trim().isEmpty()) {

            throw new RuntimeException(
                    "Document type is required"
            );
        }

        DocumentType type =
                parseDocumentType(documentType);

        validateFile(file, type);

        try {

            Path directory =
                    Paths.get(uploadDirectory)
                            .toAbsolutePath()
                            .normalize();

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            String originalFileName =
                    sanitizeFileName(
                            file.getOriginalFilename()
                    );

            String extension =
                    getFileExtension(originalFileName);

            String storedFileName =
                    UUID.randomUUID() + extension;

            Path storedFilePath =
                    directory
                            .resolve(storedFileName)
                            .normalize();

            if (!storedFilePath.startsWith(directory)) {
                throw new RuntimeException(
                        "Invalid file path"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    storedFilePath
            );

            EmployeeDocument document =
                    new EmployeeDocument();

            document.setDocumentName(
                    documentName.trim()
            );

            document.setDocumentType(
                    type.name()
            );

            document.setFileName(
                    originalFileName
            );

            document.setFileType(
                    file.getContentType()
            );

            document.setFilePath(
                    storedFilePath.toString()
            );

            document.setStatus(
                    "PENDING"
            );

            document.setEmployee(
                    employee
            );

            document.setUploadedBy(
                    uploadedBy
            );

            document.setVerifiedBy(null);

            document.setVerifiedAt(null);

            document.setRejectionReason(null);

            document.setVersion(1);

            EmployeeDocument savedDocument =
                    documentRepository.save(
                            document
                    );

            notifyEmployee(
                    savedDocument,
                    "Document Uploaded",
                    "Your document \""
                            + savedDocument.getDocumentName()
                            + "\" has been uploaded and is pending verification."
            );

            if (notifyHrAndAdmin) {

                notifyHrAndAdmin(
                        savedDocument
                );
            }

            return convertToDto(
                    savedDocument
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload document",
                    e
            );
        }
    }

    @Override
    public EmployeeDocumentDto updateDocument(
            Long id,
            EmployeeDocumentDto documentDto) {

        throw new RuntimeException(
                "Document metadata cannot be edited. A rejected document must be replaced with a new file."
        );
    }

    @Override
    public EmployeeDocumentDto verifyDocument(
            Long id) {

        if (!isAdmin() && !isHr()) {

            throw new RuntimeException(
                    "Only Admin or HR can verify documents"
            );
        }

        EmployeeDocument document =
                documentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"
                                )
                        );

        if (!"PENDING".equalsIgnoreCase(
                document.getStatus())) {

            throw new RuntimeException(
                    "Only PENDING documents can be verified"
            );
        }

        User loggedInUser =
                getLoggedInUser();

        document.setStatus(
                "VERIFIED"
        );

        document.setVerifiedBy(
                loggedInUser
        );

        document.setVerifiedAt(
                LocalDateTime.now()
        );

        document.setRejectionReason(
                null
        );

        document.setUpdatedAt(
                LocalDateTime.now()
        );

        EmployeeDocument verifiedDocument =
                documentRepository.save(
                        document
                );

        notifyEmployee(
                verifiedDocument,
                "Document Verified",
                "Your document \""
                        + verifiedDocument.getDocumentName()
                        + "\" has been verified successfully."
        );

        return convertToDto(
                verifiedDocument
        );
    }

    @Override
    public EmployeeDocumentDto rejectDocument(
            Long id,
            String rejectionReason) {

        if (!isAdmin() && !isHr()) {

            throw new RuntimeException(
                    "Only Admin or HR can reject documents"
            );
        }

        if (rejectionReason == null ||
                rejectionReason.trim().isEmpty()) {

            throw new RuntimeException(
                    "Rejection reason is required"
            );
        }

        EmployeeDocument document =
                documentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"
                                )
                        );

        if (!"PENDING".equalsIgnoreCase(
                document.getStatus())) {

            throw new RuntimeException(
                    "Only PENDING documents can be rejected"
            );
        }

        document.setStatus(
                "REJECTED"
        );

        document.setRejectionReason(
                rejectionReason.trim()
        );

        document.setVerifiedBy(
                null
        );

        document.setVerifiedAt(
                null
        );

        document.setUpdatedAt(
                LocalDateTime.now()
        );

        EmployeeDocument rejectedDocument =
                documentRepository.save(
                        document
                );

        notifyEmployee(
                rejectedDocument,
                "Document Rejected",
                "Your document \""
                        + rejectedDocument.getDocumentName()
                        + "\" has been rejected. Reason: "
                        + rejectedDocument.getRejectionReason()
        );

        return convertToDto(
                rejectedDocument
        );
    }

    @Override
    public EmployeeDocumentDto replaceDocument(
            Long id,
            MultipartFile file,
            String documentName,
            String documentType) {

        if (!isEmployee()) {

            throw new RuntimeException(
                    "Only Employee can replace a document"
            );
        }

        EmployeeDocument existingDocument =
                documentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"
                                )
                        );

        checkEmployeeAccess(
                existingDocument.getEmployee()
        );

        if (!"REJECTED".equalsIgnoreCase(
                existingDocument.getStatus())) {

            throw new RuntimeException(
                    "Only REJECTED documents can be replaced"
            );
        }

        if (documentName == null ||
                documentName.trim().isEmpty()) {

            throw new RuntimeException(
                    "Document name is required"
            );
        }

        if (documentType == null ||
                documentType.trim().isEmpty()) {

            throw new RuntimeException(
                    "Document type is required"
            );
        }

        DocumentType type =
                parseDocumentType(
                        documentType
                );

        validateFile(
                file,
                type
        );

        Path directory =
                Paths.get(uploadDirectory)
                        .toAbsolutePath()
                        .normalize();

        try {

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            String originalFileName =
                    sanitizeFileName(
                            file.getOriginalFilename()
                    );

            String extension =
                    getFileExtension(
                            originalFileName
                    );

            String storedFileName =
                    UUID.randomUUID()
                            + extension;

            Path newFilePath =
                    directory
                            .resolve(storedFileName)
                            .normalize();

            if (!newFilePath.startsWith(directory)) {

                throw new RuntimeException(
                        "Invalid file path"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    newFilePath
            );

            String oldFilePath =
                    existingDocument.getFilePath();

            int currentVersion =
                    existingDocument.getVersion() == null
                            ? 1
                            : existingDocument.getVersion();

            existingDocument.setDocumentName(
                    documentName.trim()
            );

            existingDocument.setDocumentType(
                    type.name()
            );

            existingDocument.setFileName(
                    originalFileName
            );

            existingDocument.setFileType(
                    file.getContentType()
            );

            existingDocument.setFilePath(
                    newFilePath.toString()
            );

            existingDocument.setStatus(
                    "PENDING"
            );

            existingDocument.setUploadedBy(
                    getLoggedInUser()
            );

            existingDocument.setUploadedAt(
                    LocalDateTime.now()
            );

            existingDocument.setUpdatedAt(
                    LocalDateTime.now()
            );

            existingDocument.setVerifiedBy(
                    null
            );

            existingDocument.setVerifiedAt(
                    null
            );

            existingDocument.setRejectionReason(
                    null
            );

            existingDocument.setVersion(
                    currentVersion + 1
            );

            EmployeeDocument replacedDocument =
                    documentRepository.save(
                            existingDocument
                    );

            deletePhysicalFile(
                    oldFilePath
            );

            notifyEmployee(
                    replacedDocument,
                    "Document Replaced",
                    "Your rejected document \""
                            + replacedDocument.getDocumentName()
                            + "\" has been replaced successfully and is pending verification."
            );

            return convertToDto(
                    replacedDocument
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to replace document",
                    e
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDocumentDto getDocumentById(
            Long id) {

        EmployeeDocument document =
                documentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"
                                )
                        );

        checkViewAccess(document);

        return convertToDto(
                document
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentDto> getAllDocuments() {

        if (isEmployee()) {
            return getMyDocuments();
        }

        if (isManager()) {
            return getTeamDocuments();
        }

        if (isAdmin() || isHr()) {

            return documentRepository.findAll()
                    .stream()
                    .map(this::convertToDto)
                    .toList();
        }

        throw new RuntimeException(
                "You are not authorized to view documents"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentDto> getMyDocuments() {

        Employee employee =
                getLoggedInEmployee();

        return documentRepository
                .findByEmployeeIdOrderByUploadedAtDesc(
                        employee.getId()
                )
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDocumentDto> getTeamDocuments() {

        if (!isManager()) {

            throw new RuntimeException(
                    "Only Managers can access team documents"
            );
        }

        Employee manager =
                getLoggedInEmployee();

        List<Project> managedProjects =
                projectRepository.findByManager_Id(
                        manager.getId()
                );

        if (managedProjects.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> employeeIds =
                new HashSet<>();

        for (Project project :
                managedProjects) {

            if (project.getEmployees() == null) {
                continue;
            }

            for (Employee employee :
                    project.getEmployees()) {

                if (employee != null &&
                        employee.getId() != null) {

                    employeeIds.add(
                            employee.getId()
                    );
                }
            }
        }

        if (employeeIds.isEmpty()) {
            return new ArrayList<>();
        }

        return documentRepository.findAll()
                .stream()
                .filter(document ->
                        document.getEmployee() != null &&
                                document.getEmployee().getId() != null &&
                                employeeIds.contains(
                                        document.getEmployee().getId()
                                )
                )
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getDocumentFile(
            Long id) {

        EmployeeDocument document =
                documentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"
                                )
                        );

        checkViewAccess(document);

        try {

            Path filePath =
                    Paths.get(
                                    document.getFilePath()
                            )
                            .toAbsolutePath()
                            .normalize();

            Path directory =
                    Paths.get(uploadDirectory)
                            .toAbsolutePath()
                            .normalize();

            if (!filePath.startsWith(directory)) {

                throw new RuntimeException(
                        "Invalid document path"
                );
            }

            if (!Files.exists(filePath)) {

                throw new RuntimeException(
                        "Document file not found"
                );
            }

            if (!Files.isRegularFile(filePath) ||
                    !Files.isReadable(filePath)) {

                throw new RuntimeException(
                        "Document file cannot be read"
                );
            }

            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );

            if (!resource.exists() ||
                    !resource.isReadable()) {

                throw new RuntimeException(
                        "Document file cannot be read"
                );
            }

            return resource;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load document file",
                    e
            );
        }
    }

    @Override
    public void deleteDocument(
            Long id) {

        if (!isAdmin()) {

            throw new RuntimeException(
                    "Only Admin can delete documents"
            );
        }

        EmployeeDocument document =
                documentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"
                                )
                        );

        Employee employee =
                document.getEmployee();

        Long documentId =
                document.getId();

        String documentName =
                document.getDocumentName();

        String filePath =
                document.getFilePath();

        documentRepository.delete(
                document
        );

        deletePhysicalFile(
                filePath
        );

        notifyEmployee(
                employee,
                documentId,
                "Document Deleted",
                "Your document \""
                        + documentName
                        + "\" has been deleted."
        );
    }

    private void checkViewAccess(
            EmployeeDocument document) {

        if (document == null ||
                document.getEmployee() == null) {

            throw new RuntimeException(
                    "Document employee information not found"
            );
        }

        if (isAdmin() || isHr()) {
            return;
        }

        if (isEmployee()) {

            checkEmployeeAccess(
                    document.getEmployee()
            );

            return;
        }

        if (isManager()) {

            if (!isEmployeeInManagedProjects(
                    document.getEmployee()
            )) {

                throw new RuntimeException(
                        "You can access only documents of employees in your managed projects"
                );
            }

            return;
        }

        throw new RuntimeException(
                "You are not authorized to access this document"
        );
    }

    private boolean isEmployeeInManagedProjects(
            Employee employee) {

        if (employee == null ||
                employee.getId() == null) {

            return false;
        }

        Employee manager =
                getLoggedInEmployee();

        List<Project> managedProjects =
                projectRepository.findByManager_Id(
                        manager.getId()
                );

        return managedProjects
                .stream()
                .anyMatch(project ->
                        project.getEmployees() != null &&
                                project.getEmployees()
                                        .stream()
                                        .anyMatch(projectEmployee ->
                                                projectEmployee != null &&
                                                        projectEmployee.getId() != null &&
                                                        projectEmployee.getId()
                                                                .equals(
                                                                        employee.getId()
                                                                )
                                        )
                );
    }

    private void checkEmployeeAccess(
            Employee employee) {

        Employee loggedInEmployee =
                getLoggedInEmployee();

        if (employee == null ||
                employee.getId() == null ||
                loggedInEmployee.getId() == null ||
                !employee.getId()
                        .equals(
                                loggedInEmployee.getId()
                        )) {

            throw new RuntimeException(
                    "You can access only your own documents"
            );
        }
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

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().isBlank()) {

            throw new RuntimeException(
                    "User is not authenticated"
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

    private DocumentType parseDocumentType(
            String documentType) {

        try {

            return DocumentType.valueOf(
                    documentType
                            .trim()
                            .toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            throw new RuntimeException(
                    "Invalid document type: "
                            + documentType
            );
        }
    }

    private void validateFile(
            MultipartFile file,
            DocumentType documentType) {

        if (file == null ||
                file.isEmpty()) {

            throw new RuntimeException(
                    "File is required"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                contentType.isBlank()) {

            throw new RuntimeException(
                    "File type could not be determined"
            );
        }

        if (documentType == DocumentType.PHOTO) {

            if (!"image/jpeg".equalsIgnoreCase(
                    contentType
            ) &&
                    !"image/png".equalsIgnoreCase(
                            contentType
                    )) {

                throw new RuntimeException(
                        "Photo must be JPG, JPEG or PNG"
                );
            }

        } else {

            if (!"application/pdf"
                    .equalsIgnoreCase(
                            contentType
                    )) {

                throw new RuntimeException(
                        "Only PDF files are allowed for documents"
                );
            }
        }
    }

    private String sanitizeFileName(
            String fileName) {

        if (fileName == null ||
                fileName.isBlank()) {

            throw new RuntimeException(
                    "Invalid file name"
            );
        }

        String sanitized =
                Paths.get(fileName)
                        .getFileName()
                        .toString()
                        .replaceAll(
                                "[^a-zA-Z0-9._-]",
                                "_"
                        );

        if (sanitized.isBlank()) {

            throw new RuntimeException(
                    "Invalid file name"
            );
        }

        return sanitized;
    }

    private String getFileExtension(
            String fileName) {

        int index =
                fileName.lastIndexOf('.');

        if (index < 0) {
            return "";
        }

        return fileName.substring(
                index
        );
    }

    private void deletePhysicalFile(
            String filePath) {

        if (filePath == null ||
                filePath.isBlank()) {
            return;
        }

        try {

            Path path =
                    Paths.get(filePath)
                            .toAbsolutePath()
                            .normalize();

            Path directory =
                    Paths.get(uploadDirectory)
                            .toAbsolutePath()
                            .normalize();

            if (!path.startsWith(directory)) {
                return;
            }

            Files.deleteIfExists(path);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Document record deleted but file could not be deleted",
                    e
            );
        }
    }

    private void notifyEmployee(
            EmployeeDocument document,
            String title,
            String message) {

        if (document == null) {
            return;
        }

        notifyEmployee(
                document.getEmployee(),
                document.getId(),
                title,
                message
        );
    }

    private void notifyEmployee(
            Employee employee,
            Long documentId,
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
                NotificationType.DOCUMENT,
                "EMPLOYEE_DOCUMENT",
                documentId
        );
    }

    private void notifyHrAndAdmin(
            EmployeeDocument document) {

        if (document == null ||
                document.getEmployee() == null) {

            return;
        }

        String employeeName =
                document.getEmployee().getEmployeeName();

        String documentName =
                document.getDocumentName();

        Long documentId =
                document.getId();

        String title =
                "New Document Uploaded";

        String message =
                "Employee \""
                        + employeeName
                        + "\" has uploaded a new document \""
                        + documentName
                        + "\". The document is pending verification.";

        List<User> users =
                userRepository.findAll();

        for (User user : users) {

            if (user == null ||
                    user.getId() == null) {

                continue;
            }

            if (document.getUploadedBy() != null &&
                    document.getUploadedBy().getId() != null &&
                    user.getId().equals(
                            document.getUploadedBy().getId()
                    )) {

                continue;
            }

            if (isAdminUser(user) ||
                    isHrUser(user)) {

                notificationService.createNotification(
                        user.getId(),
                        title,
                        message,
                        NotificationType.DOCUMENT,
                        "EMPLOYEE_DOCUMENT",
                        documentId
                );
            }
        }
    }

    private boolean isAdminUser(
            User user) {

        return user != null &&
                user.getRole() != null &&
                user.getRole().getRoleName()
                        == RoleType.ADMIN;
    }

    private boolean isHrUser(
            User user) {

        return user != null &&
                user.getRole() != null &&
                user.getRole().getRoleName()
                        == RoleType.HR;
    }

    private EmployeeDocumentDto convertToDto(
            EmployeeDocument document) {

        EmployeeDocumentDto dto =
                new EmployeeDocumentDto();

        dto.setId(
                document.getId()
        );

        dto.setDocumentName(
                document.getDocumentName()
        );

        if (document.getDocumentType() != null) {

            dto.setDocumentType(
                    parseDocumentType(
                            document.getDocumentType()
                    )
            );
        }

        dto.setFileName(
                document.getFileName()
        );

        dto.setFileType(
                document.getFileType()
        );

        dto.setStatus(
                document.getStatus()
        );

        dto.setDescription(
                document.getDescription()
        );

        dto.setUploadedAt(
                document.getUploadedAt()
        );

        dto.setUpdatedAt(
                document.getUpdatedAt()
        );

        dto.setVerifiedAt(
                document.getVerifiedAt()
        );

        dto.setRejectionReason(
                document.getRejectionReason()
        );

        dto.setVersion(
                document.getVersion()
        );

        if (document.getEmployee() != null) {

            dto.setEmployeeId(
                    document.getEmployee().getId()
            );

            dto.setEmployeeName(
                    document.getEmployee()
                            .getEmployeeName()
            );
        }

        if (document.getUploadedBy() != null) {

            dto.setUploadedByName(
                    document.getUploadedBy()
                            .getEmail()
            );
        }

        if (document.getVerifiedBy() != null) {

            dto.setVerifiedByName(
                    document.getVerifiedBy()
                            .getEmail()
            );
        }

        return dto;
    }

    private boolean isEmployee() {

        return hasRole(
                "ROLE_EMPLOYEE"
        );
    }

    private boolean isManager() {

        return hasRole(
                "ROLE_MANAGER"
        );
    }

    private boolean isAdmin() {

        return hasRole(
                "ROLE_ADMIN"
        );
    }

    private boolean isHr() {

        return hasRole(
                "ROLE_HR"
        );
    }

    private boolean hasRole(
            String role) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null &&
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                role.equals(
                                        authority.getAuthority()
                                )
                        );
    }
}