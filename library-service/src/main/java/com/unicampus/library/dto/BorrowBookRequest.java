package com.unicampus.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data required to borrow a book.
 */
@Schema(description = "Request body for borrowing a book")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookRequest {

    @Schema(description = "ID of the student borrowing the book")
    @NotNull(message = "Student ID is required")
    private UUID studentId;
}
