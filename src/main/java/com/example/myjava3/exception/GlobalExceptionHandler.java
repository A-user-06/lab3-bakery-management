package com.example.myjava3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import com.example.myjava3.exception.RefreshReuseException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Внутренняя ошибка сервера: " + ex.getMessage()));
    }

    @ExceptionHandler(com.example.myjava3.exception.RefreshReuseException.class)
    public ResponseEntity<Map<String, String>> handleReuse(com.example.myjava3.exception.RefreshReuseException ex) {
        return ResponseEntity.status(401).body(Map.of("error", "refresh reuse"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() == null ? "" : ex.getMessage();
        if (msg.equals("Conflict")) return ResponseEntity.status(409).body(Map.of("error", msg));
        if (msg.equals("Bad credentials") || msg.contains("token") || msg.contains("refresh")
                || msg.contains("Expired") || msg.contains("Owner") || msg.contains("Revoked")
                || msg.contains("User not found")) {
            return ResponseEntity.status(401).body(Map.of("error", msg));
        }
        return ResponseEntity.status(400).body(Map.of("error", msg));

    }


}
