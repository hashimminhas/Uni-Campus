package com.unicampus.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The data a client sends when registering a new student.
 *
 * <p>This object is received from the HTTP request body (JSON).
 * Before the service layer even sees it, Spring automatically validates every field
 * using the annotations below. If anything is missing or wrong, a 400 Bad Request
 * is returned immediately — no database call is made.
 *
 * <p>Validation rules:
 * <ul>
 *   <li>{@code firstName}, {@code lastName}, {@code program} — must not be blank.</li>
 *   <li>{@code email} — must not be blank AND must be a valid email format (e.g. a@b.com).</li>
 *   <li>{@code enrollmentYear} — must be present (cannot be null), e.g. 2024.</li>
 *   <li>{@code phoneNumber} — optional, no rules.</li>
 * </ul>
 *
 * <p>Connected to:
 * <ul>
 *   <li>StudentController (Step 6) — receives this object in the POST /students endpoint.</li>
 *   <li>StudentService (Step 5) — uses this data to build a {@link com.unicampus.student.domain.Student}
 *       entity and save it to the database.</li>
 *   <li>{@link com.unicampus.student.exception.GlobalExceptionHandler} — catches validation
 *       failures and returns the field-level error messages to the caller.</li>
 * </ul>
 */
@Schema(description = "Request body for registering a new student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentRequest {

    @Schema(description = "Student's first name")
    @NotBlank
    private String firstName;

    @Schema(description = "Student's last name")
    @NotBlank
    private String lastName;

    @Schema(description = "Student's email address")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Student's phone number")
    private String phoneNumber;

    @Schema(description = "Academic program name")
    @NotBlank
    private String program;

    @Schema(description = "Year of enrollment")
    @NotNull
    private Integer enrollmentYear;
}
