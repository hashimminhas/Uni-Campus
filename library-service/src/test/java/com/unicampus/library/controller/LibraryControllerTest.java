package com.unicampus.library.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicampus.library.dto.AddBookRequest;
import com.unicampus.library.dto.BookLoanResponse;
import com.unicampus.library.dto.BookResponse;
import com.unicampus.library.dto.BorrowBookRequest;
import com.unicampus.library.exception.BookNotAvailableException;
import com.unicampus.library.exception.BookNotFoundException;
import com.unicampus.library.exception.StudentValidationException;
import com.unicampus.library.service.LibraryService;

@WebMvcTest(LibraryController.class)
public class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addBook_valid_returns201() throws Exception {
        AddBookRequest request = new AddBookRequest("Effective Java");
        BookResponse response = BookResponse.builder()
                .bookId(UUID.randomUUID())
                .title("Effective Java")
                .isAvailable(true)
                .build();

        Mockito.when(libraryService.addBook(any(AddBookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/library/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    void getAllBooks_returns200() throws Exception {
        Mockito.when(libraryService.getAllBooks()).thenReturn(List.of());

        mockMvc.perform(get("/api/library/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void borrowBook_valid_returns201() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        BorrowBookRequest request = new BorrowBookRequest(studentId);
        
        BookLoanResponse response = BookLoanResponse.builder()
                .loanId(UUID.randomUUID())
                .bookId(bookId)
                .studentId(studentId)
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        Mockito.when(libraryService.borrowBook(eq(bookId), any(BorrowBookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/library/books/{id}/borrow", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loanId").exists())
                .andExpect(jsonPath("$.studentId").value(studentId.toString()));
    }

    @Test
    void borrowBook_studentInvalid_returns403() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        BorrowBookRequest request = new BorrowBookRequest(studentId);

        Mockito.when(libraryService.borrowBook(eq(bookId), any(BorrowBookRequest.class)))
                .thenThrow(new StudentValidationException(studentId));

        mockMvc.perform(post("/api/library/books/{id}/borrow", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Student validation failed for id: " + studentId));
    }

    @Test
    void borrowBook_bookNotAvailable_returns409() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        BorrowBookRequest request = new BorrowBookRequest(studentId);

        Mockito.when(libraryService.borrowBook(eq(bookId), any(BorrowBookRequest.class)))
                .thenThrow(new BookNotAvailableException(bookId));

        mockMvc.perform(post("/api/library/books/{id}/borrow", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void borrowBook_bookNotFound_returns404() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        BorrowBookRequest request = new BorrowBookRequest(studentId);

        Mockito.when(libraryService.borrowBook(eq(bookId), any(BorrowBookRequest.class)))
                .thenThrow(new BookNotFoundException(bookId));

        mockMvc.perform(post("/api/library/books/{id}/borrow", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void returnBook_valid_returns200() throws Exception {
        UUID loanId = UUID.randomUUID();
        BookLoanResponse response = BookLoanResponse.builder()
                .loanId(loanId)
                .bookId(UUID.randomUUID())
                .bookTitle("Effective Java")
                .studentId(UUID.randomUUID())
                .dueDate(LocalDate.now().plusDays(14))
                .returnedAt(java.time.LocalDateTime.now())
                .build();

        Mockito.when(libraryService.returnBook(eq(loanId))).thenReturn(response);

        mockMvc.perform(put("/api/library/loans/{id}/return", loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(loanId.toString()))
                .andExpect(jsonPath("$.returnedAt").exists());
    }

    @Test
    void getStudentLoans_returns200() throws Exception {
        UUID studentId = UUID.randomUUID();

        Mockito.when(libraryService.getStudentLoans(eq(studentId))).thenReturn(List.of());

        mockMvc.perform(get("/api/library/loans/student/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

}
