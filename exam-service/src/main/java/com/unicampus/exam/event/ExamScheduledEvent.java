package com.unicampus.exam.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduledEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID examId;
    private UUID courseId;
    private LocalDateTime examDate;
    private String location;
    private Integer durationMinutes;
}
