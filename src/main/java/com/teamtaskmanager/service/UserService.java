package com.teamtaskmanager.service;

import com.teamtaskmanager.model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    void deleteUserById(Long id);
}