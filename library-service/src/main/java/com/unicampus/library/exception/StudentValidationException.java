package com.unicampus.library.exception;

import java.util.UUID;

/**
 * Thrown when student identity verification with Student Service fails.
 */
public class StudentValidationException extends RuntimeException {
    public StudentValidationException(UUID studentId) {
        super("Student validation failed for id: " + studentId);
    }
}
