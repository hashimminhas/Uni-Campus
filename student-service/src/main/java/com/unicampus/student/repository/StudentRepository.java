package com.unicampus.student.repository;

import com.unicampus.student.domain.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles all database operations for the {@link Student} entity.
 *
 * <p>By extending {@code CrudRepository}, this interface gets the following methods
 * for free — no code needed:
 * <ul>
 *   <li>{@code save(student)}         – insert or update a student row</li>
 *   <li>{@code findById(uuid)}        – look up one student by their ID</li>
 *   <li>{@code findAll()}             – return every student</li>
 *   <li>{@code deleteById(uuid)}      – remove a student by ID</li>
 *   <li>{@code existsById(uuid)}      – check if a student exists</li>
 * </ul>
 *
 * <p>{@code findByEmail} is the only custom method. Spring reads the method name
 * and automatically generates the SQL {@code SELECT * FROM students WHERE email = ?}.
 * It is used to check for duplicate emails before creating a new student.
 *
 * <p>Connected to:
 * <ul>
 *   <li>{@link Student} — the entity this repository manages.</li>
 *   <li>StudentService (Step 5) — the service layer calls this repository to read/write data.</li>
 * </ul>
 */
public interface StudentRepository extends CrudRepository<Student, UUID> {

    Optional<Student> findByEmail(String email);
}
