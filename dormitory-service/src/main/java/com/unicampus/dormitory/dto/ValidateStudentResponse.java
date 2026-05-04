package com.unicampus.dormitory.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ValidateStudentResponse {
    private UUID studentId;
    private boolean isValid;
    private String academicStatus;
}
