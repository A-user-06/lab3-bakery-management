package com.example.myjava3.controller;

import com.example.myjava3.model.Role;
import com.example.myjava3.model.User;
import com.example.myjava3.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request) {
        // 1. Проверка уникальности
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Пользователь с таким email уже существует"));
        }

        // 2. Проверка надежности пароля (минимум 6 символов)
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Пароль должен содержать минимум 6 символов"));
        }

        // 3. Создание нового пользователя (принудительно ставим роль USER!)
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Хэшируем пароль
        newUser.setRole(Role.USER); // Запрещаем регистрироваться сразу админами
        newUser.setActive(true);

        userRepo.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Пользователь успешно зарегистрирован"));
    }

    // Эндпоинт для получения CSRF-токена
    @GetMapping("/csrf")
    public ResponseEntity<?> getCsrfToken(CsrfToken token) {
        return ResponseEntity.ok(Map.of("token", token.getToken()));
    }
}