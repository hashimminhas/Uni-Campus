package com.unicampus.course.integration;

public record StudentValidationResponse(
        Boolean isValid,
        String status,
        String message
) {
}
