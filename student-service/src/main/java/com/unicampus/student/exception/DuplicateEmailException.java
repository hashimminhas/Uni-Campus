package com.unicampus.student.exception;

/**
 * Thrown when a create or update request supplies an email address that already
 * belongs to another student. The controller advice catches this and returns a
 * 409 Conflict response to the client.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Student already exists with email: " + email);
    }
}
