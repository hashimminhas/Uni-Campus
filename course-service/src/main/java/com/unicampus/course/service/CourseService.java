package com.unicampus.course.service;

import com.unicampus.course.domain.CourseStatus;
import com.unicampus.course.dto.CourseCreateRequest;
import com.unicampus.course.dto.CourseResponse;
import com.unicampus.course.dto.EnrollmentResponse;

import java.util.List;
import java.util.UUID;

public interface CourseService {

    CourseResponse createCourse(CourseCreateRequest request);

    List<CourseResponse> getCourses(CourseStatus status, String semester);

    CourseResponse getCourseById(UUID courseId);

    EnrollmentResponse enrollStudent(UUID courseId, String studentId);

    EnrollmentResponse dropStudent(UUID courseId, String studentId);

    List<CourseResponse> getStudentCourses(String studentId);
}
