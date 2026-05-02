package com.unicampus.student.dto;

import com.unicampus.student.domain.AcademicStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * The data sent back when another service asks "is this student valid?".
 *
 * <p>Other microservices (e.g. Course Service, Exam Service) need to check whether
 * a student exists and is allowed to enrol or sit an exam. Instead of sharing the
 * full student profile, this small response gives them just what they need:
 * <ul>
 *   <li>{@code studentId}     – confirms which student was checked.</li>
 *   <li>{@code isValid}       – true if the student exists and their status is ACTIVE.</li>
 *   <li>{@code academicStatus} – lets the calling service know the exact status
 *       (e.g. SUSPENDED) so it can show a meaningful error to the user.</li>
 * </ul>
 *
 * <p>Connected to:
 * <ul>
 *   <li>{@link com.unicampus.student.domain.AcademicStatus} — the status field uses this enum.</li>
 *   <li>StudentService (Step 5) — builds this object for the GET /students/{id}/validate endpoint.</li>
 *   <li>StudentController (Step 6) — exposes the validate endpoint that other services call.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateStudentResponse {

    private UUID studentId;

    @Schema(description = "True if student status is ACTIVE")
    private boolean isValid;

    @Schema(description = "Current academic status of the student")
    private AcademicStatus academicStatus;
}
