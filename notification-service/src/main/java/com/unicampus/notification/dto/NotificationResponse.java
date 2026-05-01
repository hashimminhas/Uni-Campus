package com.unicampus.notification.dto;

/**
 * The full details of a notification returned to the caller after it is sent or fetched.
 * Includes everything: who it was for, how it was delivered, what it said, its status, and both timestamps.
 */
import com.unicampus.notification.domain.NotificationChannel;
import com.unicampus.notification.domain.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID notificationId;
    private UUID recipientId;
    private NotificationChannel channel;
    private String subject;
    private String body;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
