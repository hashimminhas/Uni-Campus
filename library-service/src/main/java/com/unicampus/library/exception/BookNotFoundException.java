package com.unicampus.library.exception;

import java.util.UUID;

/**
 * Thrown when a book lookup by ID finds no matching record.
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(UUID id) {
        super("Book not found with id: " + id);
    }
}
