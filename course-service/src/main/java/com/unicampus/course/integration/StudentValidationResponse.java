package com.unicampus.course.integration;

public record StudentValidationResponse(
        Boolean valid,
        String status,
        String message
) {
}
