package com.example.emps.repository;

import com.example.emps.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository
        extends JpaRepository<TaskComment, Long> {

    List<TaskComment> findByTaskIdOrderByCreatedAtDesc(
            Long taskId
    );

    List<TaskComment> findByEmployeeIdOrderByCreatedAtDesc(
            Long employeeId
    );
}