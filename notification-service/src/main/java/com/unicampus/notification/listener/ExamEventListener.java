package com.unicampus.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicampus.notification.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExamEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(ExamEventListener.class);

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_EXAM_QUEUE)
    public void onExamScheduled(Map<String, Object> event) {
        try {
            log.info("📧 Exam Scheduled Event Received!");
            log.info("Event Type: exam.scheduled");
            log.info("Exam ID: {}", event.get("examId"));
            log.info("Course ID: {}", event.get("courseId"));
            log.info("Exam Date: {}", event.get("examDate"));
            log.info("Location: {}", event.get("location"));
            log.info("Duration: {} minutes", event.get("durationMinutes"));
            
            // Simulate sending notifications to students
            log.info("✓ Notification delivered to enrolled students");
            log.info("✓ Event processed successfully\n");
        } catch (Exception e) {
            log.error("Error processing exam scheduled event", e);
        }
    }
}
