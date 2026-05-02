package com.unicampus.course.repository;

import com.unicampus.course.domain.Enrollment;
import com.unicampus.course.domain.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByCourseCourseIdAndStudentIdAndStatus(UUID courseId, String studentId, EnrollmentStatus status);

    Optional<Enrollment> findByCourseCourseIdAndStudentIdAndStatus(UUID courseId, String studentId, EnrollmentStatus status);

    long countByCourseCourseIdAndStatus(UUID courseId, EnrollmentStatus status);
}
