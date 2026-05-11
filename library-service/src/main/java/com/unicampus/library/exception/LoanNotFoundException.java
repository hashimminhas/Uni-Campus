package com.unicampus.library.exception;

import java.util.UUID;

/**
 * Thrown when a loan lookup by ID finds no matching record.
 */
public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(UUID id) {
        super("Loan not found with id: " + id);
    }
}
