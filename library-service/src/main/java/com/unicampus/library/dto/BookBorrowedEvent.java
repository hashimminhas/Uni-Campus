package com.unicampus.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookBorrowedEvent {
    private UUID studentId;
    private String bookTitle;
    private String dueDate;
}
