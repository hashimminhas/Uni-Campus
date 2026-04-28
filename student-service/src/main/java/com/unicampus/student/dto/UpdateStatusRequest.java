package com.unicampus.student.dto;

import com.unicampus.student.domain.AcademicStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The data a client sends when changing a student's academic status.
 *
 * <p>This is a focused, single-purpose request — it only carries one field.
 * It is used on the PATCH /students/{id}/status endpoint, which exists separately
 * from the general update endpoint because changing a status (e.g. suspending a
 * student) is a distinct administrative action.
 *
 * <p>The {@code academicStatus} field must be one of: ACTIVE, SUSPENDED, GRADUATED, ON_LEAVE.
 * Sending an invalid value (e.g. "UNKNOWN") will automatically return a 400 Bad Request.
 *
 * <p>Connected to:
 * <ul>
 *   <li>{@link com.unicampus.student.domain.AcademicStatus} — the allowed values come from this enum.</li>
 *   <li>StudentController (Step 6) — receives this on PATCH /students/{id}/status.</li>
 *   <li>StudentService (Step 5) — updates the student's status in the database.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull
    private AcademicStatus academicStatus;
}
