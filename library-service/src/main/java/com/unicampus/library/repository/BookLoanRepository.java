package com.unicampus.library.repository;

import com.unicampus.library.domain.Book;
import com.unicampus.library.domain.BookLoan;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations for the BookLoan entity.
 */
public interface BookLoanRepository extends CrudRepository<BookLoan, UUID> {

    /**
     * Finds all active loans for a specific student.
     */
    List<BookLoan> findByStudentIdAndReturnedAtIsNull(UUID studentId);

    /**
     * Finds all active loans for a specific book (should typically be only one).
     */
    List<BookLoan> findByBookAndReturnedAtIsNull(Book book);

    /**
     * Finds all overdue loans (returnedAt is null and dueDate is in the past).
     */
    List<BookLoan> findByReturnedAtIsNullAndDueDateBefore(LocalDate date);
}
