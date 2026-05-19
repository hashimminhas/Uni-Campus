package com.unicampus.notification.service;

/**
 * The brain of the notification feature — all business logic for sending and retrieving notifications lives here.
 * When a notification is sent, it is saved to the database first, then immediately marked as SENT (simulating delivery).
 * Callers can look up a single notification by ID, get all notifications for a student, or just check the delivery status.
 * If a requested notification ID does not exist in the database, the service throws a 404 error automatically.
 * Private helper methods keep the mapping between database records and API responses clean and separate.
 */
import com.unicampus.notification.client.StudentServiceClient;
import com.unicampus.notification.domain.Notification;
import com.unicampus.notification.domain.NotificationStatus;
import com.unicampus.notification.dto.NotificationResponse;
import com.unicampus.notification.dto.NotificationStatusResponse;
import com.unicampus.notification.dto.SendNotificationRequest;
import com.unicampus.notification.exception.NotificationNotFoundException;
import com.unicampus.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StudentServiceClient studentServiceClient;

    public NotificationResponse sendNotification(SendNotificationRequest request) {
        studentServiceClient.getStudent(request.getRecipientId());

        Notification notification = mapToEntity(request);
        notification = notificationRepository.save(notification);

        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notification = notificationRepository.save(notification);

        return mapToResponse(notification);
    }

    public NotificationResponse getNotificationById(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        return mapToResponse(notification);
    }

    public List<NotificationResponse> getNotificationsByStudentId(UUID studentId) {
        return notificationRepository.findByRecipientId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteNotification(UUID notificationId) {
        if (!notificationRepository.existsById(notificationId))
            throw new NotificationNotFoundException(notificationId);
        notificationRepository.deleteById(notificationId);
    }

    public NotificationStatusResponse getNotificationStatus(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        return NotificationStatusResponse.builder()
                .notificationId(notification.getNotificationId())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .build();
    }

    private Notification mapToEntity(SendNotificationRequest request) {
        return Notification.builder()
                .recipientId(request.getRecipientId())
                .channel(request.getChannel())
                .subject(request.getSubject())
                .body(request.getBody())
                .build();
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .recipientId(notification.getRecipientId())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .body(notification.getBody())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
