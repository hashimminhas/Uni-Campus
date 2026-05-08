package com.unicampus.exam.service;

import com.unicampus.exam.entity.ExamSchedule;
import com.unicampus.exam.event.ExamScheduledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.exam:exam.events}")
    private String examExchange;

    @Value("${spring.rabbitmq.routing.scheduled:exam.scheduled}")
    private String scheduledRoutingKey;

    @Value("${spring.rabbitmq.routing.notify:exam.notify}")
    private String notifyRoutingKey;

    public void publishExamScheduled(ExamSchedule exam) {
        ExamScheduledEvent event = ExamScheduledEvent.builder()
                .examId(exam.getExamId())
                .courseId(exam.getCourseId())
                .examDate(exam.getExamDate())
                .location(exam.getLocation())
                .durationMinutes(exam.getDurationMinutes())
                .build();

        rabbitTemplate.convertAndSend(examExchange, scheduledRoutingKey, event);
        log.info("Published exam scheduled event for exam {}", exam.getExamId());
    }

    public void publishExamNotification(ExamSchedule exam, List<String> studentIds) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("examId", exam.getExamId());
        notification.put("courseId", exam.getCourseId());
        notification.put("examDate", exam.getExamDate());
        notification.put("location", exam.getLocation());
        notification.put("durationMinutes", exam.getDurationMinutes());
        notification.put("studentIds", studentIds);

        rabbitTemplate.convertAndSend(examExchange, notifyRoutingKey, notification);
        log.info("Published exam notification for exam {} to {} students", exam.getExamId(), studentIds.size());
    }
}
