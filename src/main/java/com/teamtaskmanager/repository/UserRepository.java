package com.teamtaskmanager.repository;

import com.teamtaskmanager.model.Task;
import com.teamtaskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String Email);

}
