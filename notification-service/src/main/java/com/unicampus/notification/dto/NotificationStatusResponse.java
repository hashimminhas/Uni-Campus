package com.unicampus.notification.dto;

/**
 * A lightweight response used when the caller only wants to check if a notification was delivered.
 * Returns just the ID, current status, and the time it was sent — nothing else to keep it minimal.
 */
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
public class NotificationStatusResponse {

    private UUID notificationId;
    private NotificationStatus status;
    private LocalDateTime sentAt;
}
