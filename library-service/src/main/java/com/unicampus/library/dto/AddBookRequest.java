package com.unicampus.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data required to add a new book to the library.
 */
@Schema(description = "Request body for adding a new book")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBookRequest {

    @Schema(description = "Title of the book", example = "Clean Code")
    @NotBlank(message = "Book title is required")
    private String title;
}
