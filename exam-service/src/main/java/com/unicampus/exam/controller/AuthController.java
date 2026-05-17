package com.unicampus.exam.controller;

import com.unicampus.exam.dto.LoginRequest;
import com.unicampus.exam.dto.LoginResponse;
import com.unicampus.exam.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User login and token generation")
public class AuthController {

    private final JwtUtil jwtUtil;

    // Hardcoded demo users
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";
    private static final String STUDENT_USER = "student";
    private static final String STUDENT_PASS = "student123";
    private static final String TEACHER_USER = "teacher";
    private static final String TEACHER_PASS = "teacher123";

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and receive JWT token")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String role = null;

        // Validate credentials
        if (ADMIN_USER.equals(username) && ADMIN_PASS.equals(password)) {
            role = "ADMIN";
        } else if (STUDENT_USER.equals(username) && STUDENT_PASS.equals(password)) {
            role = "STUDENT";
        } else if (TEACHER_USER.equals(username) && TEACHER_PASS.equals(password)) {
            role = "TEACHER";
        } else {
            return ResponseEntity.status(401).build();
        }

        String token = jwtUtil.generateToken(username, role);
        return ResponseEntity.ok(new LoginResponse(token, username, role));
    }

    @GetMapping("/demo-users")
    @Operation(summary = "Get demo users", description = "Get list of demo users for testing")
    public ResponseEntity<String> getDemoUsers() {
        return ResponseEntity.ok("Demo Users:\n" +
                "1. ADMIN: username='admin', password='admin123'\n" +
                "2. STUDENT: username='student', password='student123'\n" +
                "3. TEACHER: username='teacher', password='teacher123'");
    }
}
