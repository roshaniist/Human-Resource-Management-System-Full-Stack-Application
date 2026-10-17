package com.example.emps.service;

import com.example.emps.dto.ProjectDto;

import java.util.List;

public interface ProjectService {

    ProjectDto createProject(ProjectDto projectDto);

    ProjectDto updateProject(Long id, ProjectDto projectDto);

    ProjectDto getProjectById(Long id);

    List<ProjectDto> getAllProjects();

    List<ProjectDto> getMyProjectsByEmail(String email);

    void deleteProject(Long id);
}