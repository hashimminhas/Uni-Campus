package com.unicampus.library.service;

import com.unicampus.library.domain.BookLoan;
import com.unicampus.library.dto.BookOverdueEvent;
import com.unicampus.library.repository.BookLoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OverdueCheckService {

    private final BookLoanRepository bookLoanRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0 0 1 * * ?") // Runs daily at 1 AM
    public void checkOverdueLoans() {
        log.info("Starting scheduled overdue loan check");
        LocalDate today = LocalDate.now();
        List<BookLoan> overdueLoans = bookLoanRepository.findByReturnedAtIsNullAndDueDateBefore(today);

        for (BookLoan loan : overdueLoans) {
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), today);

            BookOverdueEvent event = BookOverdueEvent.builder()
                    .studentId(loan.getStudentId())
                    .bookId(loan.getBook().getBookId())
                    .bookTitle(loan.getBook().getTitle())
                    .loanId(loan.getLoanId())
                    .dueDate(loan.getDueDate())
                    .daysOverdue(daysOverdue)
                    .build();

            rabbitTemplate.convertAndSend("library.events", "book.overdue", event);
            log.info("Published book.overdue event for loanId={}", loan.getLoanId());
        }
        log.info("Finished overdue loan check, published {} events", overdueLoans.size());
    }
}
