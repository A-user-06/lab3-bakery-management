package com.example.myjava3.repository;

import com.example.myjava3.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends BaseRepo<User, Long> {
}