package com.teamtaskmanager.service.impl;

import com.teamtaskmanager.model.User;
import com.teamtaskmanager.repository.UserRepository;

import com.teamtaskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}