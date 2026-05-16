package com.unicampus.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamScheduledEvent {
    private UUID studentId;
    private String courseCode;
    private String courseName;
    private String examDate;
    private String roomNumber;
}
