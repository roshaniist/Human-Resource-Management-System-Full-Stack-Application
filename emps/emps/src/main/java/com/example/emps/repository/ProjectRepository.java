package com.example.emps.repository;

import com.example.emps.entity.Project;
import com.example.emps.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByEmployees_Id(Long employeeId);

    List<Project> findByManager_Id(Long managerId);

    long countByStatus(ProjectStatus status);
}