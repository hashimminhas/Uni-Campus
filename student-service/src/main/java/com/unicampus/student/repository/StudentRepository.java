package com.unicampus.student.repository;

import com.unicampus.student.domain.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends CrudRepository<Student, UUID> {

    Optional<Student> findByEmail(String email);
}
