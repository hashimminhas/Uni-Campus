package com.unicampus.student.service;

import com.unicampus.student.domain.AcademicStatus;
import com.unicampus.student.domain.Student;
import com.unicampus.student.dto.*;
import com.unicampus.student.exception.DuplicateEmailException;
import com.unicampus.student.exception.StudentNotFoundException;
import com.unicampus.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

/**
 * This class handles all the business logic for managing students.
 * When a request comes in (create, update, delete, find a student), this class decides what to do.
 * It checks rules like "no two students can share the same email" before touching the database.
 * It sits between the controller (which receives requests) and the repository (which talks to the database).
 * Spring Boot creates this automatically because of the @Service annotation.
 */
@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    public List<StudentResponse> getAllStudents() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::mapToResponse)
                .toList();
    }

    public StudentResponse createStudent(CreateStudentRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException(request.getEmail());
        }
        Student student = mapToEntity(request);
        Student saved = repository.save(student);
        return mapToResponse(saved);
    }

    public StudentResponse getStudentById(UUID studentId) {
        Student student = repository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return mapToResponse(student);
    }

    public StudentResponse updateStudent(UUID studentId, UpdateStudentRequest request) {
        Student student = repository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        if (request.getEmail() != null && !request.getEmail().equals(student.getEmail())) {
            if (repository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateEmailException(request.getEmail());
            }
        }

        if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
        if (request.getLastName() != null) student.setLastName(request.getLastName());
        if (request.getEmail() != null) student.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) student.setPhoneNumber(request.getPhoneNumber());
        if (request.getProgram() != null) student.setProgram(request.getProgram());
        if (request.getEnrollmentYear() != null) student.setEnrollmentYear(request.getEnrollmentYear());

        return mapToResponse(repository.save(student));
    }

    public void deleteStudent(UUID studentId) {
        Student student = repository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        repository.delete(student);
    }

    public StudentResponse updateStatus(UUID studentId, UpdateStatusRequest request) {
        Student student = repository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        student.setAcademicStatus(request.getAcademicStatus());
        return mapToResponse(repository.save(student));
    }

    public ValidateStudentResponse validateStudent(UUID studentId) {
        Student student = repository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return ValidateStudentResponse.builder()
                .studentId(student.getStudentId())
                .isValid(student.getAcademicStatus() == AcademicStatus.ACTIVE)
                .academicStatus(student.getAcademicStatus())
                .build();
    }

    /**
     * These two private methods are translators between different data formats.
     * mapToEntity turns the data the client sent into a Student object the database understands.
     * mapToResponse turns a Student from the database into a clean response to send back to the client.
     * We never send the raw database object directly — we always convert it first.
     * This keeps the API response clean and prevents internal database fields from leaking out.
     */
    private Student mapToEntity(CreateStudentRequest request) {
        return Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .program(request.getProgram())
                .enrollmentYear(request.getEnrollmentYear())
                .build();
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .program(student.getProgram())
                .enrollmentYear(student.getEnrollmentYear())
                .academicStatus(student.getAcademicStatus())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }
}
