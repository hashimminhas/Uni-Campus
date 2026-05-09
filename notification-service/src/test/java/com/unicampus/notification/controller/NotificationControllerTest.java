package com.unicampus.notification.controller;

import com.unicampus.notification.client.StudentServiceClient;
import com.unicampus.notification.domain.NotificationChannel;
import com.unicampus.notification.domain.NotificationStatus;
import com.unicampus.notification.dto.NotificationResponse;
import com.unicampus.notification.exception.NotificationNotFoundException;
import com.unicampus.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private StudentServiceClient studentServiceClient;

    @Test
    void sendNotification_valid_returns201() throws Exception {
        UUID recipientId = UUID.randomUUID();
        NotificationResponse response = NotificationResponse.builder()
                .notificationId(UUID.randomUUID())
                .recipientId(recipientId)
                .channel(NotificationChannel.EMAIL)
                .subject("Welcome to UniCampus")
                .body("Your account has been created.")
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationService.sendNotification(any())).thenReturn(response);

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"recipientId\":\"" + recipientId + "\",\"channel\":\"EMAIL\",\"subject\":\"Welcome to UniCampus\",\"body\":\"Your account has been created.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void getNotificationById_exists_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        NotificationResponse response = NotificationResponse.builder()
                .notificationId(id)
                .recipientId(UUID.randomUUID())
                .channel(NotificationChannel.EMAIL)
                .subject("Test")
                .body("Test body")
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationService.getNotificationById(id)).thenReturn(response);

        mockMvc.perform(get("/notifications/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(id.toString()));
    }

    @Test
    void getNotificationById_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        when(notificationService.getNotificationById(id)).thenThrow(new NotificationNotFoundException(id));

        mockMvc.perform(get("/notifications/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNotificationsByStudentId_returns200() throws Exception {
        UUID studentId = UUID.randomUUID();
        List<NotificationResponse> responses = List.of(
                NotificationResponse.builder()
                        .notificationId(UUID.randomUUID())
                        .recipientId(studentId)
                        .channel(NotificationChannel.EMAIL)
                        .subject("First")
                        .body("First body")
                        .status(NotificationStatus.SENT)
                        .sentAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build(),
                NotificationResponse.builder()
                        .notificationId(UUID.randomUUID())
                        .recipientId(studentId)
                        .channel(NotificationChannel.IN_APP)
                        .subject("Second")
                        .body("Second body")
                        .status(NotificationStatus.SENT)
                        .sentAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        when(notificationService.getNotificationsByStudentId(studentId)).thenReturn(responses);

        mockMvc.perform(get("/notifications/student/" + studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
