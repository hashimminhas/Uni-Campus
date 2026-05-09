package com.unicampus.notification.controller;

import com.unicampus.notification.dto.TemplateResponse;
import com.unicampus.notification.exception.TemplateNotFoundException;
import com.unicampus.notification.service.TemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TemplateController.class)
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TemplateService templateService;

    @Test
    void createTemplate_valid_returns201() throws Exception {
        TemplateResponse response = TemplateResponse.builder()
                .templateId(UUID.randomUUID())
                .name("enrollment-confirmation")
                .subject("Enrollment Confirmed")
                .bodyTemplate("Dear student, you have been enrolled in {courseName}.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(templateService.createTemplate(any())).thenReturn(response);

        mockMvc.perform(post("/notifications/templates")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"name\":\"enrollment-confirmation\",\"subject\":\"Enrollment Confirmed\",\"bodyTemplate\":\"Dear student, you have been enrolled in {courseName}.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("enrollment-confirmation"));
    }

    @Test
    void getAllTemplates_returns200() throws Exception {
        List<TemplateResponse> responses = List.of(
                TemplateResponse.builder()
                        .templateId(UUID.randomUUID())
                        .name("enrollment-confirmation")
                        .subject("Enrollment Confirmed")
                        .bodyTemplate("Body 1")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                TemplateResponse.builder()
                        .templateId(UUID.randomUUID())
                        .name("exam-reminder")
                        .subject("Exam Scheduled")
                        .bodyTemplate("Body 2")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        when(templateService.getAllTemplates()).thenReturn(responses);

        mockMvc.perform(get("/notifications/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateTemplate_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        when(templateService.updateTemplate(eq(id), any())).thenThrow(new TemplateNotFoundException(id));

        mockMvc.perform(put("/notifications/templates/" + id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"subject\":\"Updated Subject\"}"))
                .andExpect(status().isNotFound());
    }
}
