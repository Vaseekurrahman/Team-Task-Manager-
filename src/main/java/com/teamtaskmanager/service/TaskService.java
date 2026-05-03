package com.teamtaskmanager.service;

import com.teamtaskmanager.model.Task;

import java.util.List;

public interface TaskService {

    Task saveTask(Task task);

    java.util.List<Task> getAllTasks();

    void deleteTask(Long id);
    Task getTaskById(Long id);

    List<Task> getTasksByUser(Long userId);
}