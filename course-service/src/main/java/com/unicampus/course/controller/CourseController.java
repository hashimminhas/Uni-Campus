package com.unicampus.course.controller;

import com.unicampus.course.domain.CourseStatus;
import com.unicampus.course.dto.CourseCreateRequest;
import com.unicampus.course.dto.CourseResponse;
import com.unicampus.course.dto.EnrollmentRequest;
import com.unicampus.course.dto.EnrollmentResponse;
import com.unicampus.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/courses")
    @Operation(summary = "R4: Create a new course")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @GetMapping("/courses")
    @Operation(summary = "Get all courses with optional filters")
    public ResponseEntity<List<CourseResponse>> getCourses(
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(required = false) String semester
    ) {
        return ResponseEntity.ok(courseService.getCourses(status, semester));
    }

    @GetMapping("/courses/{courseId}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @PostMapping("/courses/{courseId}/enroll")
    @Operation(summary = "R5 and R8: Enroll student and publish confirmation event")
    public ResponseEntity<EnrollmentResponse> enrollStudent(
            @PathVariable UUID courseId,
            @Valid @RequestBody EnrollmentRequest request
    ) {
        return ResponseEntity.ok(courseService.enrollStudent(courseId, request.getStudentId()));
    }

    @DeleteMapping("/courses/{courseId}/enroll/{studentId}")
    @Operation(summary = "R6 and R7: Drop course and publish drop event")
    public ResponseEntity<EnrollmentResponse> dropStudent(
            @PathVariable UUID courseId,
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(courseService.dropStudent(courseId, studentId));
    }

    @GetMapping("/students/{studentId}/courses")
    @Operation(summary = "Get all courses where student is actively enrolled")
    public ResponseEntity<List<CourseResponse>> getStudentCourses(@PathVariable String studentId) {
        return ResponseEntity.ok(courseService.getStudentCourses(studentId));
    }
}
