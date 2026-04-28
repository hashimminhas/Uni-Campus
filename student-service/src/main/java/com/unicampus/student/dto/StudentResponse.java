package com.unicampus.student.dto;

import com.unicampus.student.domain.AcademicStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The data sent back to the client after any successful student operation.
 *
 * <p>This is what the API returns as JSON — not the raw database entity.
 * Keeping a separate response object means we can control exactly what the
 * client sees. For example, if we later added a password field to Student,
 * it would never accidentally appear here.
 *
 * <p>Contains all 10 student fields: the generated ID, personal info, academic
 * info, current status, and both timestamps.
 *
 * <p>Connected to:
 * <ul>
 *   <li>{@link com.unicampus.student.domain.Student} — the service copies data from the
 *       entity into this DTO before returning it to the controller.</li>
 *   <li>{@link com.unicampus.student.domain.AcademicStatus} — the status field uses this enum.</li>
 *   <li>StudentService (Step 5) — builds this object and returns it.</li>
 *   <li>StudentController (Step 6) — wraps this in a {@code ResponseEntity} and sends it to the client.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private UUID studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String program;
    private Integer enrollmentYear;
    private AcademicStatus academicStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
