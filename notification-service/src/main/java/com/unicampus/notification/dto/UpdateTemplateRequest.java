package com.unicampus.notification.dto;

/**
 * Used to update an existing template. All fields are optional — only the ones you send will be changed.
 * This allows partial updates, e.g. changing just the subject without touching the name or body.
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemplateRequest {

    private String name;
    private String subject;
    private String bodyTemplate;
}
