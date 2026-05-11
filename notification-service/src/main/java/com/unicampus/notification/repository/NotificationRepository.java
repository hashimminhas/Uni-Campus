package com.unicampus.notification.repository;

/**
 * The database access layer for notifications. Handles saving, finding, and deleting notification records.
 * The extra method findByRecipientId lets us fetch all notifications sent to one specific student by their ID.
 */
import com.unicampus.notification.domain.Notification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends CrudRepository<Notification, UUID> {

    List<Notification> findByRecipientId(UUID recipientId);
}
