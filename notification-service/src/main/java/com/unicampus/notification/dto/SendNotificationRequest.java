package com.unicampus.notification.dto;

/**
 * The data a caller must send to trigger a new notification.
 * All four fields are required — who to notify, how to deliver it, the subject line, and the message body.
 */
import com.unicampus.notification.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

    @NotNull
    private UUID recipientId;

    @NotNull
    private NotificationChannel channel;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;
}
