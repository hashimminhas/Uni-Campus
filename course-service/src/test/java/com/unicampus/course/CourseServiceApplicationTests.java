package com.unicampus.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicampus.course.controller.CourseController;
import com.unicampus.course.domain.Course;
import com.unicampus.course.domain.CourseStatus;
import com.unicampus.course.domain.Enrollment;
import com.unicampus.course.domain.EnrollmentStatus;
import com.unicampus.course.messaging.CourseEventPublisher;
import com.unicampus.course.repository.CourseRepository;
import com.unicampus.course.repository.EnrollmentRepository;
import com.unicampus.course.service.CourseServiceImpl;
import com.unicampus.course.integration.StudentServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@Import(CourseServiceImpl.class)
class CourseServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    @MockBean
    private StudentServiceClient studentServiceClient;

    @MockBean
    private CourseEventPublisher courseEventPublisher;

    @Test
    void enrollStudent_happyPath_returns200() throws Exception {
    UUID courseId = UUID.randomUUID();
    String studentId = "stu-100";

    Course course = Course.builder()
        .courseId(courseId)
        .name("Enterprise Integration")
        .instructor("Dr. Sudais")
        .capacity(30)
        .credits(6)
        .semester("Spring-2026")
        .status(CourseStatus.OPEN)
        .build();

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
    when(studentServiceClient.isStudentEligible(studentId)).thenReturn(true);
    when(enrollmentRepository.existsByCourseCourseIdAndStudentIdAndStatus(courseId, studentId, EnrollmentStatus.ACTIVE))
        .thenReturn(false);
    when(enrollmentRepository.countByCourseCourseIdAndStatus(courseId, EnrollmentStatus.ACTIVE)).thenReturn(0L);
    when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/courses/{courseId}/enroll", courseId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new EnrollmentRequestPayload(studentId))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Enrollment successful"))
        .andExpect(jsonPath("$.studentId").value(studentId))
        .andExpect(jsonPath("$.status").value("ACTIVE"));

    verify(courseEventPublisher).publishEnrollmentConfirmed(course, studentId);
    }

    @Test
    void enrollStudent_whenStudentInvalid_returns400() throws Exception {
    UUID courseId = UUID.randomUUID();
    String studentId = "stu-999";

    Course course = Course.builder()
        .courseId(courseId)
        .name("Distributed Systems")
        .instructor("Dr. Sudais")
        .capacity(25)
        .credits(5)
        .semester("Spring-2026")
        .status(CourseStatus.OPEN)
        .build();

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
    when(studentServiceClient.isStudentEligible(studentId)).thenReturn(false);

    mockMvc.perform(post("/courses/{courseId}/enroll", courseId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new EnrollmentRequestPayload(studentId))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("Student is not eligible for enrollment"));
    }

    private record EnrollmentRequestPayload(String studentId) {
    }
}
