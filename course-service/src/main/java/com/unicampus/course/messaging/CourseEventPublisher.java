package com.unicampus.course.messaging;

import com.unicampus.course.domain.Course;

public interface CourseEventPublisher {
    void publishEnrollmentConfirmed(Course course, String studentId);

    void publishCourseDropped(Course course, String studentId);
}
