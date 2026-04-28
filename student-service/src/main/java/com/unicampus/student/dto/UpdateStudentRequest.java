package com.unicampus.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String program;
    private Integer enrollmentYear;
}
