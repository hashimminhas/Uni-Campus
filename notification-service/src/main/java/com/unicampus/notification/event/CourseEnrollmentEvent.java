package com.unicampus.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentEvent {
    private String eventType;   // "enrollment.confirmed" or "course.dropped"
    private String studentId;   // UUID as String
    private UUID courseId;
    private String courseName;
}
