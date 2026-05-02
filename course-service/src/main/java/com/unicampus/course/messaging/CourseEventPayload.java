package com.unicampus.course.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEventPayload {

    private String eventType;
    private String studentId;
    private UUID courseId;
    private String courseName;
    private OffsetDateTime occurredAt;
}
