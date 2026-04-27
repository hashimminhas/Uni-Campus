package com.unicampus.student.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

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
