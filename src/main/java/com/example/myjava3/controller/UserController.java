package com.example.myjava3.controller;

import com.example.myjava3.model.User;
import com.example.myjava3.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<User, Long, UserService> {
    public UserController(UserService service) {
        super(service);
    }
}