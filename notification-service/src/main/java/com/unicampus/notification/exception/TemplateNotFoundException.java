package com.unicampus.notification.exception;

/**
 * Thrown when a template cannot be found by its ID.
 * Causes the API to respond with a 404 Not Found error and a clear message telling the caller which ID was missing.
 */
import java.util.UUID;

public class TemplateNotFoundException extends RuntimeException {

    public TemplateNotFoundException(UUID id) {
        super("Template not found with id: " + id);
    }
}
