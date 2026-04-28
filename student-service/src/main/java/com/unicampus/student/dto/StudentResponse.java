package com.unicampus.student.dto;

import com.unicampus.student.domain.AcademicStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private UUID studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String program;
    private Integer enrollmentYear;
    private AcademicStatus academicStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
