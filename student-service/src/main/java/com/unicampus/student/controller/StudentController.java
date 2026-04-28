package com.unicampus.student.controller;

import com.unicampus.student.dto.*;
import com.unicampus.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** This is the front door of the student service — it receives HTTP requests and sends back responses.
 *  It does no logic itself; it just passes every request to StudentService and returns whatever comes back. */
@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService service;

    /** Receives a new student's details and registers them in the system.
     *  Returns the saved student profile with a 201 Created status to confirm it worked. */
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStudent(request));
    }

    /** Looks up one student by their unique ID and returns their full profile.
     *  If no student with that ID exists, a 404 Not Found error is returned automatically. */
    @GetMapping("/{studentId}")
    public StudentResponse getStudentById(@PathVariable UUID studentId) {
        return service.getStudentById(studentId);
    }

    /** Updates an existing student's profile with the fields provided in the request body.
     *  Only the fields the client sends are changed — everything else stays the same. */
    @PutMapping("/{studentId}")
    public StudentResponse updateStudent(@PathVariable UUID studentId,
                                         @RequestBody UpdateStudentRequest request) {
        return service.updateStudent(studentId, request);
    }

    /** Permanently deletes a student from the system by their ID.
     *  Returns an empty 204 No Content response to confirm the deletion was successful. */
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID studentId) {
        service.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    /** Changes a student's academic status (e.g. ACTIVE, SUSPENDED, GRADUATED, ON_LEAVE).
     *  Returns the student's updated profile showing the new status. */
    @PatchMapping("/{studentId}/status")
    public StudentResponse updateStatus(@PathVariable UUID studentId,
                                        @Valid @RequestBody UpdateStatusRequest request) {
        return service.updateStatus(studentId, request);
    }

    /** Checks whether a student is active and allowed to use other university services.
     *  Returns a simple yes/no answer (isValid) along with their current status. */
    @GetMapping("/validate/{studentId}")
    public ValidateStudentResponse validateStudent(@PathVariable UUID studentId) {
        return service.validateStudent(studentId);
    }
}
