package com.example.emps.service;

import com.example.emps.dto.EmployeeDocumentDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeDocumentService {

    EmployeeDocumentDto uploadMyDocument(
            MultipartFile file,
            String documentName,
            String documentType
    );

    EmployeeDocumentDto uploadDocumentForEmployee(
            MultipartFile file,
            String documentName,
            String documentType,
            Long employeeId
    );

    EmployeeDocumentDto updateDocument(
            Long id,
            EmployeeDocumentDto documentDto
    );

    EmployeeDocumentDto verifyDocument(
            Long id
    );

    EmployeeDocumentDto rejectDocument(
            Long id,
            String rejectionReason
    );

    EmployeeDocumentDto replaceDocument(
            Long id,
            MultipartFile file,
            String documentName,
            String documentType
    );

    EmployeeDocumentDto getDocumentById(
            Long id
    );

    List<EmployeeDocumentDto> getAllDocuments();

    List<EmployeeDocumentDto> getMyDocuments();

    List<EmployeeDocumentDto> getTeamDocuments();

    Resource getDocumentFile(
            Long id
    );

    void deleteDocument(
            Long id
    );
}