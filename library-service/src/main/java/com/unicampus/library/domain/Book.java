package com.unicampus.library.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents a book in the library catalog.
 * This is the Aggregate Root for the library's book inventory.
 */
@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID bookId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean isAvailable;

    @PrePersist
    private void prePersist() {
        if (isAvailable == null) {
            isAvailable = true;
        }
    }
}
