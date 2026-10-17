package com.example.emps.dto;

import com.example.emps.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeDocumentDto {

    private Long id;

    private String documentName;

    private DocumentType documentType;

    private String fileName;

    private String fileType;

    private String status;

    private String description;

    private LocalDateTime uploadedAt;

    private LocalDateTime updatedAt;

    private LocalDateTime verifiedAt;

    private String rejectionReason;

    private Integer version;

    private Long employeeId;

    private String employeeName;

    private String uploadedByName;

    private String verifiedByName;
}