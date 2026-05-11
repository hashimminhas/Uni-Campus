package com.unicampus.billing.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentResponse {
    private UUID studentId;
    private String academicStatus;
}
