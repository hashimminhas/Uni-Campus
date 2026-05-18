package com.unicampus.library.controller;

import com.unicampus.library.dto.*;
import com.unicampus.library.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Library", description = "Library Service APIs")
public class LibraryController {

    private final LibraryService libraryService;

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new book")
    public BookResponse addBook(@Valid @RequestBody AddBookRequest request) {
        return libraryService.addBook(request);
    }

    @PutMapping("/books/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing book")
    public BookResponse updateBook(@PathVariable("id") UUID id, @Valid @RequestBody UpdateBookRequest request) {
        return libraryService.updateBook(id, request);
    }

    @GetMapping("/books")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all books")
    public List<BookResponse> getAllBooks() {
        return libraryService.getAllBooks();
    }

    @PostMapping("/books/{id}/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Borrow a book")
    public BookLoanResponse borrowBook(@PathVariable("id") UUID id, @Valid @RequestBody BorrowBookRequest request) {
        return libraryService.borrowBook(id, request);
    }

    @PutMapping("/loans/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return a borrowed book")
    public BookLoanResponse returnBook(@PathVariable("id") UUID id) {
        return libraryService.returnBook(id);
    }

    @GetMapping("/loans/student/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get active loans for a student")
    public List<BookLoanResponse> getStudentLoans(@PathVariable("studentId") UUID studentId) {
        return libraryService.getStudentLoans(studentId);
    }

    // --- QA TESTING ENDPOINTS ---
    @Autowired
    private com.unicampus.library.service.OverdueCheckService overdueCheckService;

    @PostMapping("/test/trigger-overdue")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "QA: Manually trigger the 1AM RabbitMQ overdue cron job")
    public String triggerOverdueCron() {
        overdueCheckService.checkOverdueLoans();
        return "Overdue cron job triggered successfully! Check billing-service for fines.";
    }

    @Autowired
    private com.unicampus.library.repository.BookLoanRepository bookLoanRepository;

    @PostMapping("/test/make-all-overdue")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "QA: Force all active loans to be 5 days overdue")
    public String makeAllOverdue() {
        java.util.List<com.unicampus.library.domain.BookLoan> activeLoans = bookLoanRepository.findByStudentIdAndReturnedAtIsNull(null); // Wait, we need all active. We can use findAll
        Iterable<com.unicampus.library.domain.BookLoan> all = bookLoanRepository.findAll();
        int count = 0;
        for (com.unicampus.library.domain.BookLoan loan : all) {
            if (loan.getReturnedAt() == null) {
                loan.setDueDate(java.time.LocalDate.now().minusDays(5));
                bookLoanRepository.save(loan);
                count++;
            }
        }
        return count + " active loans have been back-dated to be 5 days overdue!";
    }
}
