package com.unicampus.notification.listener;

import com.unicampus.notification.config.RabbitMQConfig;
import com.unicampus.notification.domain.Notification;
import com.unicampus.notification.domain.NotificationChannel;
import com.unicampus.notification.domain.NotificationStatus;
import com.unicampus.notification.event.BookBorrowedEvent;
import com.unicampus.notification.event.CourseEnrollmentEvent;
import com.unicampus.notification.event.ExamScheduledEvent;
import com.unicampus.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_COURSE_QUEUE)
    public void handleCourseEvent(CourseEnrollmentEvent event) {
        log.info("Course event received — student: {}, type: {}", event.getStudentId(), event.getEventType());

        String subject, body;
        if ("course.dropped".equalsIgnoreCase(event.getEventType())) {
            subject = "Course Dropped: " + event.getCourseName();
            body    = "You have been dropped from " + event.getCourseName() + ".";
        } else {
            subject = "Enrolled in " + event.getCourseName();
            body    = "You have been successfully enrolled in " + event.getCourseName() + ".";
        }

        save(event.getStudentId(), subject, body);
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_EXAM_QUEUE)
    public void handleExamEvent(ExamScheduledEvent event) {
        log.info("Exam event received — student: {}", event.getStudentId());

        String subject = "Exam Scheduled: " + event.getCourseName();
        String body    = "Your exam for " + event.getCourseName()
                + " (" + event.getCourseCode() + ") is on " + event.getExamDate()
                + " in Room " + event.getRoomNumber() + ".";

        save(event.getStudentId().toString(), subject, body);
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_LIBRARY_QUEUE)
    public void handleBookBorrowedEvent(BookBorrowedEvent event) {
        log.info("Library event received — student: {}", event.getStudentId());

        String subject = "Book Borrowed: " + event.getBookTitle();
        String body    = "You borrowed \"" + event.getBookTitle()
                + "\". Please return it by " + event.getDueDate() + ".";

        save(event.getStudentId().toString(), subject, body);
    }

    private void save(String studentId, String subject, String body) {
        notificationRepository.save(
                Notification.builder()
                        .recipientId(java.util.UUID.fromString(studentId))
                        .channel(NotificationChannel.IN_APP)
                        .subject(subject)
                        .body(body)
                        .status(NotificationStatus.SENT)
                        .sentAt(LocalDateTime.now())
                        .build()
        );
        log.info("Notification saved — subject: {}", subject);
    }
}
