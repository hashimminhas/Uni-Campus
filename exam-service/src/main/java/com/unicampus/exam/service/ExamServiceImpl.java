package com.unicampus.exam.service;

import com.unicampus.exam.dto.ExamCreateRequest;
import com.unicampus.exam.dto.ExamResponse;
import com.unicampus.exam.entity.ExamSchedule;
import com.unicampus.exam.exception.BadRequestException;
import com.unicampus.exam.exception.NotFoundException;
import com.unicampus.exam.repository.ExamScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final ExamScheduleRepository examRepository;
    private final RestClient restClient;
    private final ExamEventPublisher eventPublisher;

    @Value("${course.service.base-url}")
    private String courseServiceBaseUrl;

    @Override
    public ExamResponse createExam(ExamCreateRequest request) {
        // Validate course exists in Course Service (sync call)
        boolean courseExists = validateCourseExists(request.getCourseId());
        if (!courseExists) {
            throw new BadRequestException("Course with ID " + request.getCourseId() + " does not exist");
        }

        // Validate exam date is in the future
        if (request.getExamDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Exam date must be in the future");
        }

        // Validate duration is positive
        if (request.getDurationMinutes() <= 0) {
            throw new BadRequestException("Duration must be positive");
        }

        // Create exam
        ExamSchedule exam = ExamSchedule.builder()
                .courseId(request.getCourseId())
                .examDate(request.getExamDate())
                .location(request.getLocation())
                .durationMinutes(request.getDurationMinutes())
                .build();

        ExamSchedule savedExam = examRepository.save(exam);
        log.info("Created exam {} for course {}", savedExam.getExamId(), savedExam.getCourseId());

        // Publish event (async)
        eventPublisher.publishExamScheduled(savedExam);

        return mapToResponse(savedExam);
    }

    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponse getExamById(UUID examId) {
        ExamSchedule exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found with ID: " + examId));
        return mapToResponse(exam);
    }

    @Override
    public List<ExamResponse> getExamsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date must be before end date");
        }
        return examRepository.findByDateRange(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponse> getExamsByCourseId(UUID courseId) {
        // Validate course exists
        boolean courseExists = validateCourseExists(courseId);
        if (!courseExists) {
            throw new NotFoundException("Course not found with ID: " + courseId);
        }

        return examRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponse> getExamsForStudent(String studentId) {
        // Get student's enrolled courses from Course Service (sync call)
        List<UUID> enrolledCourses = getStudentEnrolledCourses(studentId);

        if (enrolledCourses.isEmpty()) {
            log.warn("No enrolled courses found for student {}", studentId);
            return List.of();
        }

        // Get exams for all enrolled courses
        return enrolledCourses.stream()
                .flatMap(courseId -> examRepository.findByCourseId(courseId).stream())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void notifyStudentsForExam(UUID examId) {
        ExamSchedule exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found with ID: " + examId));

        // Get enrolled students for the course
        List<String> studentIds = getEnrolledStudents(exam.getCourseId());

        if (studentIds.isEmpty()) {
            log.warn("No students enrolled in course {} for exam {}", exam.getCourseId(), examId);
            return;
        }

        // Publish notification event (async to Notification Service)
        eventPublisher.publishExamNotification(exam, studentIds);
        log.info("Published notifications for exam {} to {} students", examId, studentIds.size());
    }

    private boolean validateCourseExists(UUID courseId) {
        try {
            String url = courseServiceBaseUrl + "/courses/" + courseId;
            restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Object.class);
            return true;
        } catch (RestClientException e) {
            log.warn("Course validation failed for course {}: {}", courseId, e.getMessage());
            return false;
        }
    }

    private List<UUID> getStudentEnrolledCourses(String studentId) {
        try {
            String url = courseServiceBaseUrl + "/students/" + studentId + "/courses";
            @SuppressWarnings("unchecked")
            List<UUID> courses = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(List.class);
            return courses != null ? courses : List.of();
        } catch (RestClientException e) {
            log.warn("Failed to get enrolled courses for student {}: {}", studentId, e.getMessage());
            return List.of();
        }
    }

    private List<String> getEnrolledStudents(UUID courseId) {
        try {
            String url = courseServiceBaseUrl + "/courses/" + courseId + "/students";
            @SuppressWarnings("unchecked")
            List<String> students = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(List.class);
            return students != null ? students : List.of();
        } catch (RestClientException e) {
            log.warn("Failed to get enrolled students for course {}: {}", courseId, e.getMessage());
            return List.of();
        }
    }

    private ExamResponse mapToResponse(ExamSchedule exam) {
        return ExamResponse.builder()
                .examId(exam.getExamId())
                .courseId(exam.getCourseId())
                .examDate(exam.getExamDate())
                .location(exam.getLocation())
                .durationMinutes(exam.getDurationMinutes())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .build();
    }
}
