package com.unicampus.exam.controller;

import com.unicampus.exam.dto.ExamCreateRequest;
import com.unicampus.exam.dto.ExamNotificationRequest;
import com.unicampus.exam.dto.ExamResponse;
import com.unicampus.exam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Tag(name = "Exam Management", description = "REST APIs for managing exam schedules")
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @Operation(summary = "Create an exam schedule linked to a course", description = "R9: Admin creates exam for existing course")
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(request));
    }

    @GetMapping
    @Operation(summary = "List all exam schedules with optional filters")
    public ResponseEntity<List<ExamResponse>> getAllExams(
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<ExamResponse> exams;
        if (courseId != null) {
            exams = examService.getExamsByCourseId(courseId);
        } else if (startDate != null && endDate != null) {
            exams = examService.getExamsByDateRange(startDate, endDate);
        } else {
            exams = examService.getAllExams();
        }
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{examId}")
    @Operation(summary = "Get exam schedule details by ID")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable UUID examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }

    @PostMapping("/{examId}/notify")
    @Operation(summary = "Notify enrolled students for an exam", description = "R12: Async notification via Notification Service")
    public ResponseEntity<Void> notifyStudentsForExam(@PathVariable UUID examId) {
        examService.notifyStudentsForExam(examId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/courses/{courseId}/exams")
    @Operation(summary = "Get all exam schedules for a specific course")
    public ResponseEntity<List<ExamResponse>> getExamsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(examService.getExamsByCourseId(courseId));
    }
}
