package com.unicampus.student.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralises exception handling for all controllers in the student service.
 *
 * <p>Each @ExceptionHandler method intercepts a specific exception type and
 * returns a structured JSON error body so clients always receive a consistent
 * error format instead of Spring's default white-label error page.
 *
 * <ul>
 *   <li>{@link StudentNotFoundException}       → 404 Not Found</li>
 *   <li>{@link DuplicateEmailException}        → 409 Conflict</li>
 *   <li>{@link MethodArgumentNotValidException} → 400 Bad Request with per-field errors</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStudentNotFound(StudentNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(DuplicateEmailException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 409);
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage(),
                        (existing, duplicate) -> existing
                ));
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Validation failed");
        body.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
