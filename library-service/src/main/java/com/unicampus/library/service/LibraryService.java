package com.unicampus.library.service;

import com.unicampus.library.domain.Book;
import com.unicampus.library.domain.BookLoan;
import com.unicampus.library.dto.*;
import com.unicampus.library.exception.*;
import com.unicampus.library.repository.BookLoanRepository;
import com.unicampus.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final BookRepository bookRepository;
    private final BookLoanRepository bookLoanRepository;
    private final WebClient studentWebClient;

    @Transactional
    public BookResponse addBook(AddBookRequest request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .isAvailable(true)
                .build();
        book = bookRepository.save(book);
        return mapToBookResponse(book);
    }

    @Transactional
    public BookResponse updateBook(UUID bookId, UpdateBookRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getIsAvailable() != null) {
            book.setIsAvailable(request.getIsAvailable());
        }
        
        book = bookRepository.save(book);
        return mapToBookResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return ((List<Book>) bookRepository.findAll()).stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookLoanResponse borrowBook(UUID bookId, BorrowBookRequest request) {
        validateStudent(request.getStudentId());

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (!Boolean.TRUE.equals(book.getIsAvailable())) {
            throw new BookNotAvailableException(bookId);
        }

        book.setIsAvailable(false);
        bookRepository.save(book);

        BookLoan loan = BookLoan.builder()
                .book(book)
                .studentId(request.getStudentId())
                .build();
        loan = bookLoanRepository.save(loan);

        return mapToLoanResponse(loan);
    }

    @Transactional
    public BookLoanResponse returnBook(UUID loanId) {
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        loan.setReturnedAt(LocalDateTime.now());
        loan = bookLoanRepository.save(loan);

        Book book = loan.getBook();
        book.setIsAvailable(true);
        bookRepository.save(book);

        return mapToLoanResponse(loan);
    }

    @Transactional(readOnly = true)
    public List<BookLoanResponse> getStudentLoans(UUID studentId) {
        return bookLoanRepository.findByStudentIdAndReturnedAtIsNull(studentId).stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    private void validateStudent(UUID studentId) {
        ValidateStudentResponse response;
        try {
            response = studentWebClient.get()
                    .uri("/students/validate/{id}", studentId)
                    .retrieve()
                    .bodyToMono(ValidateStudentResponse.class)
                    .block();
        } catch (Exception e) {
            throw new StudentValidationException(studentId);
        }

        if (response == null || !Boolean.TRUE.equals(response.getIsValid())) {
            throw new StudentValidationException(studentId);
        }
    }

    private BookResponse mapToBookResponse(Book book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .isAvailable(book.getIsAvailable())
                .build();
    }

    private BookLoanResponse mapToLoanResponse(BookLoan loan) {
        return BookLoanResponse.builder()
                .loanId(loan.getLoanId())
                .bookId(loan.getBook().getBookId())
                .bookTitle(loan.getBook().getTitle())
                .studentId(loan.getStudentId())
                .dueDate(loan.getDueDate())
                .returnedAt(loan.getReturnedAt())
                .build();
    }
}
