package com.unicampus.library.repository;

import com.unicampus.library.domain.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Handles database operations for the Book entity.
 */
public interface BookRepository extends CrudRepository<Book, UUID> {
}
