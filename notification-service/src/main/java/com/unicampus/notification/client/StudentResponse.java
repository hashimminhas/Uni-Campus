package com.unicampus.notification.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
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
    private String academicStatus;
}
