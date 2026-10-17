package com.example.emps.repository;

import com.example.emps.entity.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeDocumentRepository
        extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument> findByEmployeeId(Long employeeId);

    List<EmployeeDocument> findByEmployeeIdOrderByUploadedAtDesc(
            Long employeeId
    );

    long countByEmployeeId(Long employeeId);

}