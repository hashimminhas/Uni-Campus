package com.unicampus.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data returned when representing a book loan in the API.
 */
@Schema(description = "Response representing a book loan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookLoanResponse {

    @Schema(description = "Unique ID of the loan")
    private UUID loanId;

    @Schema(description = "ID of the borrowed book")
    private UUID bookId;

    @Schema(description = "Title of the borrowed book")
    private String bookTitle;

    @Schema(description = "ID of the student who borrowed the book")
    private UUID studentId;

    @Schema(description = "Date when the book is due for return")
    private LocalDate dueDate;

    @Schema(description = "Timestamp when the book was actually returned")
    private LocalDateTime returnedAt;
}
