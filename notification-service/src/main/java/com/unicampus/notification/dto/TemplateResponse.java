package com.unicampus.notification.dto;

/**
 * The full details of a notification template returned to the caller after create or update.
 * Includes the template ID, all text fields, and timestamps for when it was created and last changed.
 */
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
public class TemplateResponse {

    private UUID templateId;
    private String name;
    private String subject;
    private String bodyTemplate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
