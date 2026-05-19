package com.unicampus.dormitory.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AssignmentRequest {
    private UUID studentId;
    private String semester;
}
