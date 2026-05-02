package com.unicampus.student.exception;

import java.util.UUID;

/**
 * Thrown when a student lookup by ID finds no matching record in the database.
 * The controller advice catches this and returns a 404 Not Found response to the client.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(UUID id) {
        super("Student not found with id: " + id);
    }
}
