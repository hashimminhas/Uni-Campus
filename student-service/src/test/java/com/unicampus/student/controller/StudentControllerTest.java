package com.unicampus.student.controller;

import com.unicampus.student.domain.AcademicStatus;
import com.unicampus.student.dto.StudentResponse;
import com.unicampus.student.dto.ValidateStudentResponse;
import com.unicampus.student.exception.StudentNotFoundException;
import com.unicampus.student.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void createStudent_valid_returns201() throws Exception {
        StudentResponse response = StudentResponse.builder()
                .studentId(UUID.randomUUID())
                .firstName("Hashim")
                .lastName("Ali")
                .email("hashim@ut.ee")
                .program("Computer Science")
                .enrollmentYear(2024)
                .academicStatus(AcademicStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(studentService.createStudent(any())).thenReturn(response);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Hashim",
                                  "lastName": "Ali",
                                  "email": "hashim@ut.ee",
                                  "program": "Computer Science",
                                  "enrollmentYear": 2024
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Hashim"))
                .andExpect(jsonPath("$.academicStatus").value("ACTIVE"));
    }

    @Test
    void getStudentById_exists_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        StudentResponse response = StudentResponse.builder()
                .studentId(id)
                .firstName("Hashim")
                .lastName("Ali")
                .email("hashim@ut.ee")
                .program("Computer Science")
                .enrollmentYear(2024)
                .academicStatus(AcademicStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(studentService.getStudentById(id)).thenReturn(response);

        mockMvc.perform(get("/students/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(id.toString()));
    }

    @Test
    void getStudentById_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        when(studentService.getStudentById(id)).thenThrow(new StudentNotFoundException(id));

        mockMvc.perform(get("/students/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void validateStudent_active_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        ValidateStudentResponse response = ValidateStudentResponse.builder()
                .studentId(id)
                .isValid(true)
                .academicStatus(AcademicStatus.ACTIVE)
                .build();

        when(studentService.validateStudent(id)).thenReturn(response);

        mockMvc.perform(get("/students/validate/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void validateStudent_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        when(studentService.validateStudent(id)).thenThrow(new StudentNotFoundException(id));

        mockMvc.perform(get("/students/validate/{id}", id))
                .andExpect(status().isNotFound());
    }
}
