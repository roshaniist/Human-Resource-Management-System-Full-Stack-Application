package com.example.emps.controller;

import com.example.emps.dto.TaskCommentDto;
import com.example.emps.dto.TaskDto;
import com.example.emps.dto.TaskStatusUpdateRequest;
import com.example.emps.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                taskService.getTaskById(id)
        );
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskDto>> getMyTasks() {

        return ResponseEntity.ok(
                taskService.getMyTasks()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasksForCurrentUser() {

        return ResponseEntity.ok(
                taskService.getTasksForCurrentUser()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @Valid @RequestBody TaskDto taskDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        taskService.createTask(taskDto)
                );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto) {

        return ResponseEntity.ok(
                taskService.updateTask(id, taskDto)
        );
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}/status-update")
    public ResponseEntity<TaskDto> updateTaskStatusAndComment(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request) {

        return ResponseEntity.ok(
                taskService.updateTaskStatusAndComment(
                        id,
                        request
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TaskCommentDto>> getTaskComments(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                taskService.getTaskComments(id)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }
}