package com.unicampus.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data required to update an existing book.
 */
@Schema(description = "Request body for updating a book")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequest {

    @Schema(description = "Updated title of the book")
    private String title;

    @Schema(description = "Updated availability status")
    private Boolean isAvailable;
}
