package com.unicampus.student.dto;

import com.unicampus.student.domain.AcademicStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateStudentResponse {

    private UUID studentId;
    private boolean isValid;
    private AcademicStatus academicStatus;
}
