package com.unicampus.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The data a client sends when updating an existing student's profile.
 *
 * <p>This object is received from the HTTP request body (JSON) on the
 * PUT /students/{id} endpoint. All fields are optional — the client only
 * sends the fields they want to change. Fields left out will be null,
 * and the service layer is responsible for ignoring nulls and keeping
 * the existing values in the database.
 *
 * <p>There is no validation here on purpose. Unlike creating a student,
 * a partial update should not force the client to resend every field.
 *
 * <p>Connected to:
 * <ul>
 *   <li>StudentController (Step 6) — receives this on PUT /students/{id}.</li>
 *   <li>StudentService (Step 5) — checks which fields are non-null and applies
 *       only those changes to the existing {@link com.unicampus.student.domain.Student} entity.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String program;
    private Integer enrollmentYear;
}
