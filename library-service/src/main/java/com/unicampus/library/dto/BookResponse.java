package com.unicampus.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data returned when representing a book in the API.
 */
@Schema(description = "Response representing a book")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    @Schema(description = "Unique ID of the book")
    private UUID bookId;

    @Schema(description = "Title of the book")
    private String title;

    @Schema(description = "Whether the book is currently available to borrow")
    private Boolean isAvailable;
}
