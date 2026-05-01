package com.unicampus.notification.service;

/**
 * Handles all business logic for managing reusable notification templates.
 * Templates can be created once and reused many times — this service handles creating, listing, and updating them.
 * When updating, only the fields you provide are changed; anything left out stays exactly as it was (partial update).
 * If a template ID does not exist during an update, it throws a 404 error automatically.
 * Private helper methods handle converting between database records and the response shapes returned to callers.
 */
import com.unicampus.notification.domain.NotificationTemplate;
import com.unicampus.notification.dto.CreateTemplateRequest;
import com.unicampus.notification.dto.TemplateResponse;
import com.unicampus.notification.dto.UpdateTemplateRequest;
import com.unicampus.notification.exception.TemplateNotFoundException;
import com.unicampus.notification.repository.NotificationTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TemplateService {

    @Autowired
    private NotificationTemplateRepository templateRepository;

    public TemplateResponse createTemplate(CreateTemplateRequest request) {
        NotificationTemplate template = mapToEntity(request);
        template = templateRepository.save(template);
        return mapToResponse(template);
    }

    public List<TemplateResponse> getAllTemplates() {
        return StreamSupport.stream(templateRepository.findAll().spliterator(), false)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TemplateResponse updateTemplate(UUID templateId, UpdateTemplateRequest request) {
        NotificationTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException(templateId));

        if (request.getName() != null) {
            template.setName(request.getName());
        }
        if (request.getSubject() != null) {
            template.setSubject(request.getSubject());
        }
        if (request.getBodyTemplate() != null) {
            template.setBodyTemplate(request.getBodyTemplate());
        }

        template = templateRepository.save(template);
        return mapToResponse(template);
    }

    private NotificationTemplate mapToEntity(CreateTemplateRequest request) {
        return NotificationTemplate.builder()
                .name(request.getName())
                .subject(request.getSubject())
                .bodyTemplate(request.getBodyTemplate())
                .build();
    }

    private TemplateResponse mapToResponse(NotificationTemplate template) {
        return TemplateResponse.builder()
                .templateId(template.getTemplateId())
                .name(template.getName())
                .subject(template.getSubject())
                .bodyTemplate(template.getBodyTemplate())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
