package com.unicampus.notification.exception;

import java.util.UUID;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(UUID studentId) {
        super("Student not found with id: " + studentId);
    }
}
