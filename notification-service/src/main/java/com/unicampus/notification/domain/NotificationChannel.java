package com.unicampus.notification.domain;

/**
 * The two ways a notification can be delivered — either via Email or inside the app (In-App).
 * Think of it like choosing between getting an email vs. seeing a pop-up inside the platform.
 */
public enum NotificationChannel {
    EMAIL, IN_APP
}
