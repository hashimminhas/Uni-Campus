package com.unicampus.student.controller;

import com.unicampus.student.config.JwtUtil;
import com.unicampus.student.dto.StudentResponse;
import com.unicampus.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/students/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Auth", description = "Login and receive a JWT token")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final StudentService studentService;

    @Operation(summary = "Admin login — returns admin JWT token")
    @PostMapping("/admin")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> body) {
        if ("admin".equals(body.get("username")) && "admin".equals(body.get("password"))) {
            return ResponseEntity.ok(Map.of(
                    "token", jwtUtil.generateAdminToken(),
                    "role",  "ADMIN"
            ));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid admin credentials"));
    }

    @Operation(summary = "Login with student UUID — returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String studentIdStr = body.get("studentId");
        if (studentIdStr == null || studentIdStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "studentId is required"));
        }
        try {
            UUID studentId = UUID.fromString(studentIdStr);
            StudentResponse student = studentService.getStudentById(studentId);
            String token = jwtUtil.generateToken(studentId);
            return ResponseEntity.ok(Map.of(
                    "token",     token,
                    "studentId", studentId.toString(),
                    "firstName", student.getFirstName(),
                    "lastName",  student.getLastName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Student not found"));
        }
    }
}
