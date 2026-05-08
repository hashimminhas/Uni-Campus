package com.unicampus.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponse {
    private UUID examId;
    private UUID courseId;
    private LocalDateTime examDate;
    private String location;
    private Integer durationMinutes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
