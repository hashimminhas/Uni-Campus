package com.unicampus.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookOverdueEvent {
    private UUID studentId;
    private UUID bookId;
    private String bookTitle;
    private UUID loanId;
    private LocalDate dueDate;
    private long daysOverdue;
}
