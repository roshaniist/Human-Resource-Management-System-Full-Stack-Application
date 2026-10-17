package com.example.emps.repository;

import com.example.emps.entity.Task;
import com.example.emps.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByEmployeesId(Long employeeId);

    long countByProjectId(Long projectId);

    long countByStatus(TaskStatus status);
}