package com.teamtaskmanager.service;

import com.teamtaskmanager.model.Project;

import java.util.List;

public interface ProjectService {

    Project saveProject(Project project);

    List<Project> getAllProjects();

    void deleteProject(Long id);
}