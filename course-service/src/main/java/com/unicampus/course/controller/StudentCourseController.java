package com.unicampus.course.controller;

import com.unicampus.course.dto.CourseResponse;
import com.unicampus.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Student Course Controller", description = "Student course lookup API endpoints")
public class StudentCourseController {

    private final CourseService courseService;

    @GetMapping("/students/{studentId}/courses")
    @Operation(summary = "Get all courses where a student is actively enrolled")
    public ResponseEntity<List<CourseResponse>> getStudentCourses(@PathVariable String studentId) {
        return ResponseEntity.ok(courseService.getStudentCourses(studentId));
    }
}
