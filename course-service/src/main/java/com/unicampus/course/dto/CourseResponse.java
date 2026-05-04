package com.unicampus.course.dto;

import com.unicampus.course.domain.CourseStatus;
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
public class CourseResponse {

    private UUID courseId;
    private String name;
    private String instructor;
    private Integer capacity;
    private Integer credits;
    private String semester;
    private CourseStatus status;
    private Long enrolledCount;
}
