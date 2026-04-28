package com.unicampus.course.dto;

import com.unicampus.course.domain.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {

    private UUID courseId;
    private String studentId;
    private EnrollmentStatus status;
    private String message;
}
