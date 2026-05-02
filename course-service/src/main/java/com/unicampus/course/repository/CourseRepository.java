package com.unicampus.course.repository;

import com.unicampus.course.domain.Course;
import com.unicampus.course.domain.CourseStatus;
import com.unicampus.course.domain.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByStatus(CourseStatus status);

    List<Course> findBySemester(String semester);

    List<Course> findByStatusAndSemester(CourseStatus status, String semester);

    List<Course> findDistinctByEnrollmentsStudentIdAndEnrollmentsStatus(String studentId, EnrollmentStatus status);
}
