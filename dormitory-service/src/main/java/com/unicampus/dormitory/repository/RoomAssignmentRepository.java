package com.unicampus.dormitory.repository;

import com.unicampus.dormitory.model.RoomAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, UUID> {
    List<RoomAssignment> findByStudentId(UUID studentId);
    Optional<RoomAssignment> findByStudentIdAndStatus(UUID studentId, RoomAssignment.AssignmentStatus status);
}
