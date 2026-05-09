package com.unicampus.notification.dto;

/**
 * The data needed to create a new reusable notification template.
 * All three fields are required — a unique name to identify it, a subject line, and the body text.
 */
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTemplateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String subject;

    @NotBlank
    private String bodyTemplate;
}
