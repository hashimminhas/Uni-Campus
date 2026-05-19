package com.unicampus.notification.controller;

/**
 * The entry point for all notification-related API requests — this is what the outside world talks to.
 * Accepts incoming HTTP requests, hands them off to NotificationService, and sends the result back as JSON.
 * Handles sending new notifications, looking up a single notification by ID, fetching all notifications for a student,
 * and checking whether a specific notification was successfully delivered.
 * @CrossOrigin allows the frontend (running on a different port) to call these endpoints without being blocked.
 */
import com.unicampus.notification.dto.NotificationResponse;
import com.unicampus.notification.dto.NotificationStatusResponse;
import com.unicampus.notification.dto.SendNotificationRequest;
import com.unicampus.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@Tag(name = "Notifications", description = "Send and track notification delivery")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Send a notification to a student")
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.sendNotification(request));
    }

    @Operation(summary = "Get notification by ID")
    @GetMapping("/{notificationId}")
    public NotificationResponse getNotificationById(@PathVariable UUID notificationId) {
        return notificationService.getNotificationById(notificationId);
    }

    @Operation(summary = "Get all notifications for a student")
    @GetMapping("/student/{studentId}")
    public List<NotificationResponse> getNotificationsByStudentId(@PathVariable UUID studentId) {
        return notificationService.getNotificationsByStudentId(studentId);
    }

    @Operation(summary = "Delete (dismiss) a notification")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check notification delivery status")
    @GetMapping("/{notificationId}/status")
    public NotificationStatusResponse getNotificationStatus(@PathVariable UUID notificationId) {
        return notificationService.getNotificationStatus(notificationId);
    }
}
