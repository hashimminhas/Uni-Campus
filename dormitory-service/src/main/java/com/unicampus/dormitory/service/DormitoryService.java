package com.unicampus.dormitory.service;

import com.unicampus.dormitory.model.Room;
import com.unicampus.dormitory.model.RoomAssignment;
import com.unicampus.dormitory.repository.RoomAssignmentRepository;
import com.unicampus.dormitory.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DormitoryService {

    private final RoomRepository roomRepository;
    private final RoomAssignmentRepository roomAssignmentRepository;
    private final StudentServiceClient studentServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public Room addRoom(Room room) {
        room.setRoomId(UUID.randomUUID());
        room.setIsAvailable(true);
        room.setCurrentOccupancy(0);
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(UUID roomId) {
        return roomRepository.findById(roomId);
    }

    @Transactional
    public RoomAssignment assignRoom(UUID roomId, UUID studentId, String semester) {
        // 1. Verify student identity
        if (!studentServiceClient.isStudentActive(studentId)) {
            throw new IllegalArgumentException("Student is not active or does not exist");
        }

        // 2. Check if student already has an active assignment
        Optional<RoomAssignment> existingAssignment = roomAssignmentRepository.findByStudentIdAndStatus(studentId, RoomAssignment.AssignmentStatus.ACTIVE);
        if (existingAssignment.isPresent()) {
            throw new IllegalStateException("Student already has an active room assignment");
        }

        // 3. Find and validate room
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        
        if (!room.getIsAvailable() || room.getCurrentOccupancy() >= room.getCapacity()) {
            throw new IllegalStateException("Room is not available or at full capacity");
        }

        // 4. Create assignment
        RoomAssignment assignment = new RoomAssignment();
        assignment.setAssignmentId(UUID.randomUUID());
        assignment.setRoomId(roomId);
        assignment.setStudentId(studentId);
        assignment.setSemester(semester);
        assignment.setAssignmentDate(new Date());
        assignment.setStatus(RoomAssignment.AssignmentStatus.ACTIVE);
        roomAssignmentRepository.save(assignment);

        // 5. Update room
        room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
        if (room.getCurrentOccupancy() >= room.getCapacity()) {
            room.setIsAvailable(false);
        }
        roomRepository.save(room);

        // 6. Publish housing.fee.charged event to Billing Service via RabbitMQ
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("studentId", studentId.toString());
            event.put("roomId", roomId.toString());
            event.put("semester", semester);
            event.put("amount", "500.00"); // Standard housing fee amount
            event.put("timestamp", new Date());

            log.info("Publishing housing.fee.charged event: {}", event);
            rabbitTemplate.convertAndSend("billing.events", "housing.fee.charged", event);
        } catch (Exception e) {
            log.error("Failed to publish housing.fee.charged event to RabbitMQ", e);
        }

        return assignment;
    }

    public List<RoomAssignment> getAssignmentsForStudent(UUID studentId) {
        return roomAssignmentRepository.findByStudentId(studentId);
    }

    @Transactional
    public void removeAssignment(UUID assignmentId) {
        RoomAssignment assignment = roomAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        
        assignment.setStatus(RoomAssignment.AssignmentStatus.CANCELLED);
        roomAssignmentRepository.save(assignment);

        Room room = roomRepository.findById(assignment.getRoomId())
                .orElseThrow(() -> new IllegalStateException("Room associated with assignment not found"));
        
        if (room.getCurrentOccupancy() > 0) {
            room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
        }
        room.setIsAvailable(true);
        roomRepository.save(room);
        
        log.info("Assignment cancelled, billing adjustment event should be published for assignmentId: {}", assignmentId);
    }

    public boolean isStudentEligible(UUID studentId) {
        boolean isActive = studentServiceClient.isStudentActive(studentId);
        if (!isActive) return false;
        
        Optional<RoomAssignment> activeAssignment = roomAssignmentRepository.findByStudentIdAndStatus(studentId, RoomAssignment.AssignmentStatus.ACTIVE);
        return activeAssignment.isEmpty();
    }
}
