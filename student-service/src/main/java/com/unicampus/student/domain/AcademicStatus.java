package com.unicampus.student.domain;

/**
 * Represents the current standing of a student at the university.
 *
 * <p>This is used in the {@link Student} entity to track where a student is
 * in their academic journey. It is stored in the database as a plain text
 * string (e.g. "ACTIVE") so it is human-readable if you open the database directly.
 *
 * <ul>
 *   <li>ACTIVE    – enrolled and attending normally</li>
 *   <li>SUSPENDED – temporarily blocked (e.g. unpaid fees)</li>
 *   <li>GRADUATED – completed the program</li>
 *   <li>ON_LEAVE  – temporarily away but still enrolled</li>
 * </ul>
 *
 * <p>Connected to: {@link Student} (stored in the academicStatus column),
 * {@link com.unicampus.student.dto.UpdateStatusRequest} (used when changing status via API),
 * {@link com.unicampus.student.dto.StudentResponse} and
 * {@link com.unicampus.student.dto.ValidateStudentResponse} (returned to the caller).
 */
public enum AcademicStatus {
    ACTIVE, SUSPENDED, GRADUATED, ON_LEAVE
}
