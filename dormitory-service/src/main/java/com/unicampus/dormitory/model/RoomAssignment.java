package com.unicampus.dormitory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "room_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAssignment {
    @Id
    private UUID assignmentId;

    private UUID roomId;
    private UUID studentId;
    
    private String semester;
    
    @Temporal(TemporalType.DATE)
    private Date assignmentDate;
    
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    public enum AssignmentStatus {
        ACTIVE, COMPLETED, CANCELLED
    }
}
