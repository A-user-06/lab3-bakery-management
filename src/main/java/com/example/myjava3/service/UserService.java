package com.example.myjava3.service;

import com.example.myjava3.model.User;
import com.example.myjava3.repository.UserRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User, Long> {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) { this.userRepo = userRepo; }

    @Override
    protected JpaRepository<User, Long> getRepository() { return userRepo; }
}