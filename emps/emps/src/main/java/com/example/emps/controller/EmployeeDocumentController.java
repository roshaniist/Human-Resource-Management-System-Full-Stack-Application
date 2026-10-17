package com.example.emps.controller;

import com.example.emps.dto.EmployeeDocumentDto;
import com.example.emps.service.EmployeeDocumentService;

import jakarta.validation.constraints.NotBlank;

import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employee-documents")
public class EmployeeDocumentController {

    private final EmployeeDocumentService documentService;

    public EmployeeDocumentController(
            EmployeeDocumentService documentService) {

        this.documentService = documentService;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/my-documents")
    public ResponseEntity<EmployeeDocumentDto> uploadMyDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentName") @NotBlank String documentName,
            @RequestParam("documentType") @NotBlank String documentType) {

        EmployeeDocumentDto document =
                documentService.uploadMyDocument(
                        file,
                        documentName,
                        documentType
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(document);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeDocumentDto> uploadDocumentForEmployee(
            @PathVariable Long employeeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentName") @NotBlank String documentName,
            @RequestParam("documentType") @NotBlank String documentType) {

        EmployeeDocumentDto document =
                documentService.uploadDocumentForEmployee(
                        file,
                        documentName,
                        documentType,
                        employeeId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(document);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PutMapping("/{id}/verify")
    public ResponseEntity<EmployeeDocumentDto> verifyDocument(
            @PathVariable Long id) {

        EmployeeDocumentDto document =
                documentService.verifyDocument(id);

        return ResponseEntity.ok(document);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<EmployeeDocumentDto> rejectDocument(
            @PathVariable Long id,
            @RequestParam("rejectionReason") @NotBlank String rejectionReason) {

        EmployeeDocumentDto document =
                documentService.rejectDocument(
                        id,
                        rejectionReason
                );

        return ResponseEntity.ok(document);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}/replace")
    public ResponseEntity<EmployeeDocumentDto> replaceDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentName") @NotBlank String documentName,
            @RequestParam("documentType") @NotBlank String documentType) {

        EmployeeDocumentDto document =
                documentService.replaceDocument(
                        id,
                        file,
                        documentName,
                        documentType
                );

        return ResponseEntity.ok(document);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> viewDocumentFile(
            @PathVariable Long id) {

        Resource resource =
                documentService.getDocumentFile(id);

        String fileName =
                resource.getFilename();

        MediaType mediaType =
                MediaType.APPLICATION_OCTET_STREAM;

        if (fileName != null) {

            String lowerFileName =
                    fileName.toLowerCase();

            if (lowerFileName.endsWith(".pdf")) {

                mediaType =
                        MediaType.APPLICATION_PDF;

            } else if (lowerFileName.endsWith(".jpg")
                    || lowerFileName.endsWith(".jpeg")) {

                mediaType =
                        MediaType.IMAGE_JPEG;

            } else if (lowerFileName.endsWith(".png")) {

                mediaType =
                        MediaType.IMAGE_PNG;
            }
        }

        String safeFileName =
                fileName != null
                        ? fileName.replaceAll(
                        "[\\r\\n\"]",
                        "_"
                )
                        : "document";

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                safeFileName +
                                "\""
                )
                .body(resource);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDocumentDto> getDocumentById(
            @PathVariable Long id) {

        EmployeeDocumentDto document =
                documentService.getDocumentById(id);

        return ResponseEntity.ok(document);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping
    public ResponseEntity<List<EmployeeDocumentDto>>
    getAllDocuments() {

        return ResponseEntity.ok(
                documentService.getAllDocuments()
        );
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/team-documents")
    public ResponseEntity<List<EmployeeDocumentDto>>
    getTeamDocuments() {

        return ResponseEntity.ok(
                documentService.getTeamDocuments()
        );
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-documents")
    public ResponseEntity<List<EmployeeDocumentDto>>
    getMyDocuments() {

        return ResponseEntity.ok(
                documentService.getMyDocuments()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id) {

        documentService.deleteDocument(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}