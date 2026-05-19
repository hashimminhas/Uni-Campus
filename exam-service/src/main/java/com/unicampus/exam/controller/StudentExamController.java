package com.unicampus.exam.controller;

import com.unicampus.exam.dto.ExamResponse;
import com.unicampus.exam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Tag(name = "Student Exam Management", description = "REST APIs for student exam operations")
public class StudentExamController {

    private final ExamService examService;

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get exam schedules for a student's enrolled courses", description = "R10, R11: Student views their exams")
    public ResponseEntity<List<ExamResponse>> getStudentExams(@PathVariable String studentId) {
        return ResponseEntity.ok(examService.getExamsForStudent(studentId));
    }
}
