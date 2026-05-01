package com.unicampus.notification.domain;

/**
 * Tracks the current state of a notification — waiting to be sent (PENDING),
 * successfully delivered (SENT), or failed to deliver (FAILED).
 */
public enum NotificationStatus {
    PENDING, SENT, FAILED
}
