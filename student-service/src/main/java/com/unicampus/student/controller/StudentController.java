package com.unicampus.student.controller;

import com.unicampus.student.dto.*;
import com.unicampus.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** This is the front door of the student service — it receives HTTP requests and sends back responses.
 *  It does no logic itself; it just passes every request to StudentService and returns whatever comes back. */
@RestController
@RequestMapping("/students")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@Tag(name = "Student Service", description = "Central identity provider for UniCampus")
public class StudentController {

    @Autowired
    private StudentService service;

    @Operation(summary = "Get all students")
    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return service.getAllStudents();
    }

    @Operation(summary = "Register a new student")
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStudent(request));
    }

    @Operation(summary = "Get student profile by ID")
    @GetMapping("/{studentId}")
    public StudentResponse getStudentById(@PathVariable UUID studentId) {
        return service.getStudentById(studentId);
    }

    @Operation(summary = "Update student contact info and profile")
    @PutMapping("/{studentId}")
    public StudentResponse updateStudent(@PathVariable UUID studentId,
                                         @RequestBody UpdateStudentRequest request) {
        return service.updateStudent(studentId, request);
    }

    @Operation(summary = "Deactivate or remove a student")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID studentId) {
        service.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update student academic status")
    @PatchMapping("/{studentId}/status")
    public StudentResponse updateStatus(@PathVariable UUID studentId,
                                        @Valid @RequestBody UpdateStatusRequest request) {
        return service.updateStatus(studentId, request);
    }

    @Operation(summary = "Validate student is active (internal endpoint)")
    @GetMapping("/validate/{studentId}")
    public ValidateStudentResponse validateStudent(@PathVariable UUID studentId) {
        return service.validateStudent(studentId);
    }
}
