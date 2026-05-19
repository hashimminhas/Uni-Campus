package com.unicampus.dormitory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicampus.dormitory.dto.AssignmentRequest;
import com.unicampus.dormitory.model.RoomAssignment;
import com.unicampus.dormitory.service.DormitoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DormitoryController.class)
public class DormitoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DormitoryService dormitoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void assignRoom_HappyPath() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        String semester = "Fall 2026";

        AssignmentRequest request = new AssignmentRequest();
        request.setStudentId(studentId);
        request.setSemester(semester);

        RoomAssignment mockAssignment = new RoomAssignment();
        mockAssignment.setAssignmentId(UUID.randomUUID());
        mockAssignment.setRoomId(roomId);
        mockAssignment.setStudentId(studentId);
        mockAssignment.setSemester(semester);
        mockAssignment.setStatus(RoomAssignment.AssignmentStatus.ACTIVE);

        // Mock the service returning the assignment successfully
        when(dormitoryService.assignRoom(eq(roomId), eq(studentId), eq(semester)))
                .thenReturn(mockAssignment);

        mockMvc.perform(post("/dormitory/rooms/{roomId}/assign", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assignmentId").value(mockAssignment.getAssignmentId().toString()))
                .andExpect(jsonPath("$.studentId").value(studentId.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    public void assignRoom_ErrorPath_StudentNotActive() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        String semester = "Fall 2026";

        AssignmentRequest request = new AssignmentRequest();
        request.setStudentId(studentId);
        request.setSemester(semester);

        // Mock the service throwing IllegalArgumentException (e.g., student not found/active)
        when(dormitoryService.assignRoom(eq(roomId), eq(studentId), eq(semester)))
                .thenThrow(new IllegalArgumentException("Student is not active or does not exist"));

        mockMvc.perform(post("/dormitory/rooms/{roomId}/assign", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Student is not active or does not exist"));
    }

    @Test
    public void assignRoom_ErrorPath_RoomFull() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        String semester = "Fall 2026";

        AssignmentRequest request = new AssignmentRequest();
        request.setStudentId(studentId);
        request.setSemester(semester);

        // Mock the service throwing IllegalStateException (e.g., room full)
        when(dormitoryService.assignRoom(eq(roomId), eq(studentId), eq(semester)))
                .thenThrow(new IllegalStateException("Room is not available or at full capacity"));

        mockMvc.perform(post("/dormitory/rooms/{roomId}/assign", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("Room is not available or at full capacity"));
    }
}
