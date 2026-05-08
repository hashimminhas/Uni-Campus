package com.unicampus.exam.service;

import com.unicampus.exam.dto.ExamCreateRequest;
import com.unicampus.exam.dto.ExamResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExamService {
    ExamResponse createExam(ExamCreateRequest request);

    List<ExamResponse> getAllExams();

    ExamResponse getExamById(UUID examId);

    List<ExamResponse> getExamsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<ExamResponse> getExamsByCourseId(UUID courseId);

    List<ExamResponse> getExamsForStudent(String studentId);

    void notifyStudentsForExam(UUID examId);
}
