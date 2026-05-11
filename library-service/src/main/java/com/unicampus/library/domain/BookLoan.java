package com.unicampus.library.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a borrowing record for a book.
 */
@Entity
@Table(name = "book_loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private UUID studentId;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDateTime returnedAt;

    @PrePersist
    private void prePersist() {
        if (dueDate == null) {
            // Default loan period is 14 days
            dueDate = LocalDate.now().plusDays(14);
        }
    }
}
