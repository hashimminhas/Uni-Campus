package com.unicampus.library.exception;

import java.util.UUID;

/**
 * Thrown when attempting to borrow a book that is already checked out.
 */
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(UUID id) {
        super("Book is not available for borrowing: " + id);
    }
}
