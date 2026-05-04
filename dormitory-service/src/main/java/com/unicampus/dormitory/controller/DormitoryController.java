package com.unicampus.dormitory.controller;

import com.unicampus.dormitory.dto.AssignmentRequest;
import com.unicampus.dormitory.model.Room;
import com.unicampus.dormitory.model.RoomAssignment;
import com.unicampus.dormitory.service.DormitoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dormitory")
@RequiredArgsConstructor
public class DormitoryController {

    private final DormitoryService dormitoryService;

    @PostMapping("/rooms")
    public ResponseEntity<Room> addRoom(@RequestBody Room room) {
        Room createdRoom = dormitoryService.addRoom(room);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(dormitoryService.getAllRooms());
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable UUID roomId) {
        return dormitoryService.getRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rooms/{roomId}/assign")
    public ResponseEntity<?> assignRoom(@PathVariable UUID roomId, @RequestBody AssignmentRequest request) {
        try {
            RoomAssignment assignment = dormitoryService.assignRoom(roomId, request.getStudentId(), request.getSemester());
            return new ResponseEntity<>(assignment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/assignments/student/{studentId}")
    public ResponseEntity<List<RoomAssignment>> getStudentAssignments(@PathVariable UUID studentId) {
        return ResponseEntity.ok(dormitoryService.getAssignmentsForStudent(studentId));
    }

    @DeleteMapping("/assignments/{assignmentId}")
    public ResponseEntity<Void> removeAssignment(@PathVariable UUID assignmentId) {
        try {
            dormitoryService.removeAssignment(assignmentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/rooms/{roomId}/availability")
    public ResponseEntity<Boolean> checkRoomAvailability(@PathVariable UUID roomId) {
        return dormitoryService.getRoomById(roomId)
                .map(room -> ResponseEntity.ok(room.getIsAvailable() && room.getCurrentOccupancy() < room.getCapacity()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/students/{studentId}/eligibility")
    public ResponseEntity<Boolean> checkStudentEligibility(@PathVariable UUID studentId) {
        return ResponseEntity.ok(dormitoryService.isStudentEligible(studentId));
    }
}
