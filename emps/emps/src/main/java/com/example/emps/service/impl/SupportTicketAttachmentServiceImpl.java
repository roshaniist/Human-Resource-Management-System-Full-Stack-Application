package com.example.emps.service.impl;

import com.example.emps.dto.SupportTicketAttachmentDto;
import com.example.emps.entity.SupportTicket;
import com.example.emps.entity.SupportTicketAttachment;
import com.example.emps.entity.User;
import com.example.emps.enums.RoleType;
import com.example.emps.repository.SupportTicketAttachmentRepository;
import com.example.emps.repository.SupportTicketRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.service.SupportTicketAttachmentService;
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
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SupportTicketAttachmentServiceImpl
        implements SupportTicketAttachmentService {

    private final SupportTicketAttachmentRepository attachmentRepository;
    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.support-directory}")
    private String uploadDirectory;

    public SupportTicketAttachmentServiceImpl(
            SupportTicketAttachmentRepository attachmentRepository,
            SupportTicketRepository ticketRepository,
            UserRepository userRepository) {

        this.attachmentRepository = attachmentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SupportTicketAttachmentDto uploadAttachment(
            Long ticketId,
            MultipartFile file) {

        SupportTicket ticket = findTicket(ticketId);
        User currentUser = getCurrentUser();

        checkTicketAccess(ticket, currentUser);
        validateFile(file);

        try {
            Path directory = Paths.get(uploadDirectory)
                    .toAbsolutePath()
                    .normalize();

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            String originalFileName =
                    sanitizeFileName(file.getOriginalFilename());

            String extension =
                    getFileExtension(originalFileName);

            String storedFileName =
                    UUID.randomUUID() + extension;

            Path storedFilePath =
                    directory.resolve(storedFileName)
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

            SupportTicketAttachment attachment =
                    new SupportTicketAttachment();

            attachment.setTicket(ticket);
            attachment.setFileName(originalFileName);
            attachment.setFileType(file.getContentType());
            attachment.setFilePath(storedFilePath.toString());
            attachment.setUploadedBy(currentUser);

            SupportTicketAttachment savedAttachment =
                    attachmentRepository.save(attachment);

            return mapToDto(savedAttachment);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to upload attachment",
                    e
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketAttachmentDto> getTicketAttachments(
            Long ticketId) {

        SupportTicket ticket = findTicket(ticketId);
        User currentUser = getCurrentUser();

        checkTicketAccess(ticket, currentUser);

        return attachmentRepository
                .findByTicketIdOrderByUploadedAtAsc(ticketId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getAttachmentFile(
            Long attachmentId) {

        SupportTicketAttachment attachment =
                attachmentRepository.findById(attachmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Attachment not found"
                                )
                        );

        SupportTicket ticket =
                attachment.getTicket();

        User currentUser = getCurrentUser();

        checkTicketAccess(ticket, currentUser);

        try {
            Path filePath =
                    Paths.get(
                                    attachment.getFilePath()
                            )
                            .toAbsolutePath()
                            .normalize();

            Path directory =
                    Paths.get(uploadDirectory)
                            .toAbsolutePath()
                            .normalize();

            if (!filePath.startsWith(directory)) {
                throw new RuntimeException(
                        "Invalid attachment path"
                );
            }

            if (!Files.exists(filePath)) {
                throw new RuntimeException(
                        "Attachment file not found"
                );
            }

            if (!Files.isRegularFile(filePath) ||
                    !Files.isReadable(filePath)) {

                throw new RuntimeException(
                        "Attachment file cannot be read"
                );
            }

            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );

            if (!resource.exists() ||
                    !resource.isReadable()) {

                throw new RuntimeException(
                        "Attachment file cannot be read"
                );
            }

            return resource;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load attachment file",
                    e
            );
        }
    }

    @Override
    public void deleteAttachment(
            Long attachmentId) {

        SupportTicketAttachment attachment =
                attachmentRepository.findById(attachmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Attachment not found"
                                )
                        );

        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new RuntimeException(
                    "Only Admin can delete attachments"
            );
        }

        String filePath =
                attachment.getFilePath();

        attachmentRepository.delete(attachment);

        deletePhysicalFile(filePath);
    }

    private SupportTicket findTicket(Long ticketId) {

        return ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Support ticket not found"
                        )
                );
    }

    private User getCurrentUser() {

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
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );
    }

    private void checkTicketAccess(
            SupportTicket ticket,
            User user) {

        if (isAdmin(user)) {
            return;
        }

        if (ticket.getCreatedBy() != null &&
                ticket.getCreatedBy()
                        .getId()
                        .equals(user.getId())) {

            return;
        }

        if (isRole(user, RoleType.IT_SUPPORT) &&
                ticket.getAssignedTo() != null &&
                ticket.getAssignedTo()
                        .getId()
                        .equals(user.getId())) {

            return;
        }

        throw new RuntimeException(
                "You are not authorized to access this ticket attachment"
        );
    }

    private void validateFile(
            MultipartFile file) {

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

        if (!isAllowedContentType(contentType)) {

            throw new RuntimeException(
                    "Unsupported file type. Allowed: PDF, JPG, JPEG, PNG, TXT, DOC, DOCX, XLS, XLSX"
            );
        }
    }

    private boolean isAllowedContentType(
            String contentType) {

        return "application/pdf".equalsIgnoreCase(contentType)
                || "image/jpeg".equalsIgnoreCase(contentType)
                || "image/png".equalsIgnoreCase(contentType)
                || "text/plain".equalsIgnoreCase(contentType)
                || "application/msword".equalsIgnoreCase(contentType)
                || "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                .equalsIgnoreCase(contentType)
                || "application/vnd.ms-excel"
                .equalsIgnoreCase(contentType)
                || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                .equalsIgnoreCase(contentType);
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

        return fileName.substring(index);
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
                    "Attachment record deleted but file could not be deleted",
                    e
            );
        }
    }

    private SupportTicketAttachmentDto mapToDto(
            SupportTicketAttachment attachment) {

        SupportTicketAttachmentDto dto =
                new SupportTicketAttachmentDto();

        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setFileType(attachment.getFileType());
        dto.setUploadedAt(attachment.getUploadedAt());

        if (attachment.getUploadedBy() != null) {

            dto.setUploadedByName(
                    getFullName(
                            attachment.getUploadedBy()
                    )
            );
        }

        return dto;
    }

    private String getFullName(User user) {

        return user.getFirstName()
                + " "
                + user.getLastName();
    }

    private boolean isAdmin(User user) {

        return isRole(
                user,
                RoleType.ADMIN
        );
    }

    private boolean isRole(
            User user,
            RoleType roleType) {

        return user != null &&
                user.getRole() != null &&
                user.getRole().getRoleName() == roleType;
    }
}