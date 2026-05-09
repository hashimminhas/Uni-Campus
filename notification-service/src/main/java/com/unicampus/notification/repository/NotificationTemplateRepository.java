package com.unicampus.notification.repository;

/**
 * The database access layer for notification templates.
 * Uses standard CRUD operations — create, read, update, delete — provided automatically by Spring Data.
 */
import com.unicampus.notification.domain.NotificationTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface NotificationTemplateRepository extends CrudRepository<NotificationTemplate, UUID> {
}
