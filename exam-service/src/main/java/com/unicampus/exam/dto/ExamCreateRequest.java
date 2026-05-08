package com.unicampus.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamCreateRequest {
    @NotNull(message = "Course ID cannot be null")
    private UUID courseId;

    @NotNull(message = "Exam date cannot be null")
    private LocalDateTime examDate;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    @NotNull(message = "Duration in minutes cannot be null")
    private Integer durationMinutes;
}
