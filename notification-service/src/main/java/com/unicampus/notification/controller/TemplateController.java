package com.unicampus.notification.controller;

/**
 * The entry point for all template-related API requests — handles creating, listing, and updating message templates.
 * Accepts incoming HTTP requests, hands them off to TemplateService, and sends the result back as JSON.
 * Templates are reusable message blueprints — this controller lets you manage them through the API.
 * The PUT endpoint supports partial updates, so callers only need to send the fields they want to change.
 * @CrossOrigin allows the frontend (running on a different port) to call these endpoints without being blocked.
 */
import com.unicampus.notification.dto.CreateTemplateRequest;
import com.unicampus.notification.dto.TemplateResponse;
import com.unicampus.notification.dto.UpdateTemplateRequest;
import com.unicampus.notification.service.TemplateService;
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
@RequestMapping("/notifications/templates")
@CrossOrigin(origins = "*")
@Tag(name = "Notification Templates", description = "Manage reusable message templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Operation(summary = "List all message templates")
    @GetMapping
    public List<TemplateResponse> getAllTemplates() {
        return templateService.getAllTemplates();
    }

    @Operation(summary = "Create a new message template")
    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(@Valid @RequestBody CreateTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request));
    }

    @Operation(summary = "Update an existing message template")
    @PutMapping("/{templateId}")
    public TemplateResponse updateTemplate(@PathVariable UUID templateId, @RequestBody UpdateTemplateRequest request) {
        return templateService.updateTemplate(templateId, request);
    }
}
