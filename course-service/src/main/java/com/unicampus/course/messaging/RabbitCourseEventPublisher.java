package com.unicampus.course.messaging;

import com.unicampus.course.domain.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class RabbitCourseEventPublisher implements CourseEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.course-events}")
    private String exchange;

    @Value("${messaging.routing-keys.enrollment-confirmed}")
    private String enrollmentConfirmedRoutingKey;

    @Value("${messaging.routing-keys.course-dropped}")
    private String courseDroppedRoutingKey;

    @Override
    public void publishEnrollmentConfirmed(Course course, String studentId) {
        CourseEventPayload payload = CourseEventPayload.builder()
                .eventType("enrollment.confirmed")
                .studentId(studentId)
                .courseId(course.getCourseId())
                .courseName(course.getName())
                .occurredAt(OffsetDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(exchange, enrollmentConfirmedRoutingKey, payload);
    }

    @Override
    public void publishCourseDropped(Course course, String studentId) {
        CourseEventPayload payload = CourseEventPayload.builder()
                .eventType("course.dropped")
                .studentId(studentId)
                .courseId(course.getCourseId())
                .courseName(course.getName())
                .occurredAt(OffsetDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(exchange, courseDroppedRoutingKey, payload);
    }
}
