package com.unicampus.course.service;

import com.unicampus.course.domain.Course;
import com.unicampus.course.domain.CourseStatus;
import com.unicampus.course.domain.Enrollment;
import com.unicampus.course.domain.EnrollmentStatus;
import com.unicampus.course.dto.CourseCreateRequest;
import com.unicampus.course.dto.CourseResponse;
import com.unicampus.course.dto.EnrollmentResponse;
import com.unicampus.course.exception.BadRequestException;
import com.unicampus.course.exception.ConflictException;
import com.unicampus.course.exception.NotFoundException;
import com.unicampus.course.integration.StudentServiceClient;
import com.unicampus.course.messaging.CourseEventPublisher;
import com.unicampus.course.repository.CourseRepository;
import com.unicampus.course.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentServiceClient studentServiceClient;
    private final CourseEventPublisher courseEventPublisher;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) {
        Course course = Course.builder()
                .name(request.getName())
                .instructor(request.getInstructor())
                .capacity(request.getCapacity())
                .credits(request.getCredits())
                .semester(request.getSemester())
                .status(request.getStatus() == null ? CourseStatus.OPEN : request.getStatus())
                .build();

        Course savedCourse = courseRepository.save(course);
        return toResponse(savedCourse, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCourses(CourseStatus status, String semester) {
        List<Course> courses;

        if (status != null && semester != null && !semester.isBlank()) {
            courses = courseRepository.findByStatusAndSemester(status, semester);
        } else if (status != null) {
            courses = courseRepository.findByStatus(status);
        } else if (semester != null && !semester.isBlank()) {
            courses = courseRepository.findBySemester(semester);
        } else {
            courses = courseRepository.findAll();
        }

        return courses.stream()
                .map(course -> toResponse(
                        course,
                        enrollmentRepository.countByCourseCourseIdAndStatus(course.getCourseId(), EnrollmentStatus.ACTIVE)
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(UUID courseId) {
        Course course = getExistingCourse(courseId);
        long enrolledCount = enrollmentRepository.countByCourseCourseIdAndStatus(course.getCourseId(), EnrollmentStatus.ACTIVE);
        return toResponse(course, enrolledCount);
    }

    @Override
    @Transactional
    public EnrollmentResponse enrollStudent(UUID courseId, String studentId) {
        Course course = getExistingCourse(courseId);

        if (course.getStatus() != CourseStatus.OPEN) {
            throw new BadRequestException("Course is not open for enrollment");
        }

        if (!studentServiceClient.isStudentEligible(studentId)) {
            throw new BadRequestException("Student is not eligible for enrollment");
        }

        boolean alreadyEnrolled = enrollmentRepository
                .existsByCourseCourseIdAndStudentIdAndStatus(courseId, studentId, EnrollmentStatus.ACTIVE);
        if (alreadyEnrolled) {
            throw new ConflictException("Student is already enrolled in this course");
        }

        long activeEnrollments = enrollmentRepository.countByCourseCourseIdAndStatus(courseId, EnrollmentStatus.ACTIVE);
        if (activeEnrollments >= course.getCapacity()) {
            throw new ConflictException("Course capacity has been reached");
        }

        Enrollment enrollment = Enrollment.builder()
                .course(course)
                .studentId(studentId)
                .status(EnrollmentStatus.ACTIVE)
                .build();

        enrollmentRepository.save(enrollment);

        if (activeEnrollments + 1 >= course.getCapacity()) {
            course.setStatus(CourseStatus.CLOSED);
            courseRepository.save(course);
        }

        courseEventPublisher.publishEnrollmentConfirmed(course, studentId);

        return EnrollmentResponse.builder()
                .courseId(course.getCourseId())
                .studentId(studentId)
                .status(EnrollmentStatus.ACTIVE)
                .message("Enrollment successful")
                .build();
    }

    @Override
    @Transactional
    public EnrollmentResponse dropStudent(UUID courseId, String studentId) {
        Course course = getExistingCourse(courseId);

        Enrollment enrollment = enrollmentRepository
                .findByCourseCourseIdAndStudentIdAndStatus(courseId, studentId, EnrollmentStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active enrollment not found for this student"));

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);

        if (course.getStatus() == CourseStatus.CLOSED) {
            long activeEnrollments = enrollmentRepository.countByCourseCourseIdAndStatus(courseId, EnrollmentStatus.ACTIVE);
            if (activeEnrollments < course.getCapacity()) {
                course.setStatus(CourseStatus.OPEN);
                courseRepository.save(course);
            }
        }

        courseEventPublisher.publishCourseDropped(course, studentId);

        return EnrollmentResponse.builder()
                .courseId(course.getCourseId())
                .studentId(studentId)
                .status(EnrollmentStatus.DROPPED)
                .message("Course dropped successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getStudentCourses(String studentId) {
        return courseRepository.findDistinctByEnrollmentsStudentIdAndEnrollmentsStatus(studentId, EnrollmentStatus.ACTIVE)
                .stream()
                .map(course -> toResponse(
                        course,
                        enrollmentRepository.countByCourseCourseIdAndStatus(course.getCourseId(), EnrollmentStatus.ACTIVE)
                ))
                .toList();
    }

    private Course getExistingCourse(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
    }

    private CourseResponse toResponse(Course course, long enrolledCount) {
        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .name(course.getName())
                .instructor(course.getInstructor())
                .capacity(course.getCapacity())
                .credits(course.getCredits())
                .semester(course.getSemester())
                .status(course.getStatus())
                .enrolledCount(enrolledCount)
                .build();
    }
}
