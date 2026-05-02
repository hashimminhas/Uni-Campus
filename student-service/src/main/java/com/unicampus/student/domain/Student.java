package com.unicampus.student.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a student record stored in the database.
 *
 * <p>This is a JPA entity, which means Spring Boot automatically maps it to the
 * "students" table in PostgreSQL. Every field becomes a column.
 *
 * <p>Key behaviours:
 * <ul>
 *   <li>The ID ({@code studentId}) is a UUID generated automatically — you never set it yourself.</li>
 *   <li>Email must be unique across all students — the database enforces this.</li>
 *   <li>When a new student is first saved, {@code @PrePersist} runs automatically and sets
 *       {@code createdAt}, {@code updatedAt}, and defaults {@code academicStatus} to ACTIVE.</li>
 *   <li>Every time an existing student is updated, {@code @PreUpdate} refreshes {@code updatedAt}.</li>
 * </ul>
 *
 * <p>Connected to:
 * <ul>
 *   <li>{@link AcademicStatus} — the status field uses this enum.</li>
 *   <li>{@link com.unicampus.student.repository.StudentRepository} — reads and writes Student rows.</li>
 *   <li>{@link com.unicampus.student.dto.StudentResponse} — when the API returns a student,
 *       the service copies data from this entity into that DTO.</li>
 * </ul>
 */
@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID studentId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    @Column(nullable = false)
    private String program;

    @Column(nullable = false)
    private Integer enrollmentYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcademicStatus academicStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (academicStatus == null) {
            academicStatus = AcademicStatus.ACTIVE;
        }
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
