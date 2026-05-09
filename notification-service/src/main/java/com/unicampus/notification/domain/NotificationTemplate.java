package com.unicampus.notification.domain;

/**
 * A reusable message blueprint — instead of writing the same notification from scratch every time,
 * you define a template once (with a unique name, subject, and body) and reuse it whenever needed.
 * Each template name must be unique so it can be identified and looked up without confusion.
 * The database automatically records when the template was first created and when it was last updated.
 * This class maps to the "notification_templates" table in the PostgreSQL database.
 */
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID templateId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
