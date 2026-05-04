package com.unicampus.dormitory.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    private UUID roomId;

    private String roomNumber;
    private String building;
    private Integer capacity;
    private Integer currentOccupancy;
    
    @ElementCollection
    private List<String> amenities;
    
    private Boolean isAvailable;
}
