package com.unicampus.mealplan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.UUID;

@Data
public class ValidateStudentResponse {
    private UUID studentId;
    
    @JsonProperty("isValid")
    private boolean isValid;
    
    private String academicStatus;
}
