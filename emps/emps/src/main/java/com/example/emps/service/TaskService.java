package com.example.emps.service;

import com.example.emps.dto.TaskCommentDto;
import com.example.emps.dto.TaskDto;
import com.example.emps.dto.TaskStatusUpdateRequest;

import java.util.List;

public interface TaskService {

    TaskDto createTask(TaskDto taskDto);

    TaskDto updateTask(Long id, TaskDto taskDto);

    TaskDto getTaskById(Long id);

    List<TaskDto> getTasksForCurrentUser();

    List<TaskDto> getMyTasks();

    void deleteTask(Long id);

    TaskDto updateTaskStatusAndComment(
            Long taskId,
            TaskStatusUpdateRequest request
    );

    List<TaskCommentDto> getTaskComments(
            Long taskId
    );
}