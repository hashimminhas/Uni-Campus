package com.unicampus.course.dto;

import com.unicampus.course.domain.CourseStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String instructor;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    @Min(1)
    private Integer credits;

    @NotBlank
    private String semester;

    private CourseStatus status;
}
