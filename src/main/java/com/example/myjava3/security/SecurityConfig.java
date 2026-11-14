package com.example.myjava3.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Включает работу аннотаций @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Настройка CSRF (защита от подделки запросов). Токен будет храниться в Cookie.
                .csrf(csrf -> csrf.disable()) // ВРЕМЕННО ОТКЛЮЧАЕМ ДЛЯ ТЕСТОВ
                // Настройка правил доступа
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/csrf").permitAll() // <--- Добавили /csrf
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                // Включаем HTTP Basic Auth
                .httpBasic(Customizer.withDefaults())
                // Отключаем сессии (так как используем REST API)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Обработка ошибок (чтобы отдавался JSON, а не HTML)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8"); // <--- ИСПРАВЛЕНИЕ КОДИРОВКИ
                            response.getWriter().write("{\"error\": \"Требуется аутентификация. Неверный email или пароль.\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8"); // <--- ИСПРАВЛЕНИЕ КОДИРОВКИ
                            response.getWriter().write("{\"error\": \"Доступ запрещен. Недостаточно прав.\"}");
                        })
                );

        return http.build();
    }

    // Бин для шифрования паролей
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Бин менеджера аутентификации (понадобится для контроллера)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}