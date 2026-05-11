package com.unicampus.billing.service;

import com.unicampus.billing.domain.*;
import com.unicampus.billing.dto.*;
import com.unicampus.billing.exception.AccountNotFoundException;
import com.unicampus.billing.repository.BillingAccountRepository;
import com.unicampus.billing.repository.ChargeRepository;
import com.unicampus.billing.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final BillingAccountRepository accountRepository;
    private final ChargeRepository chargeRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${STUDENT_SERVICE_URL:http://localhost:8081}")
    private String studentServiceUrl;

    @Value("${COURSE_SERVICE_URL:http://localhost:8083}")
    private String courseServiceUrl;

    private static final BigDecimal COST_PER_CREDIT = new BigDecimal("500.00");

    @Transactional
    public BillingAccount getOrCreateAccount(UUID studentId) {
        return accountRepository.findByStudentId(studentId)
                .orElseGet(() -> {
                    BillingAccount newAccount = BillingAccount.builder()
                            .studentId(studentId)
                            .outstandingBalance(BigDecimal.ZERO)
                            .build();
                    return accountRepository.save(newAccount);
                });
    }

    @Transactional
    public ChargeResponse addCharge(UUID studentId, AddChargeRequest request) {
        BillingAccount account = getOrCreateAccount(studentId);

        Charge charge = Charge.builder()
                .account(account)
                .chargeType(request.getChargeType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        charge = chargeRepository.save(charge);

        account.setOutstandingBalance(account.getOutstandingBalance().add(charge.getAmount()));
        accountRepository.save(account);

        return mapToChargeResponse(charge);
    }

    @Transactional(readOnly = true)
    public List<ChargeResponse> getCharges(UUID studentId) {
        BillingAccount account = accountRepository.findByStudentId(studentId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for student " + studentId));

        return account.getCharges().stream()
                .map(this::mapToChargeResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BillingStatusResponse getStatus(UUID studentId) {
        BillingAccount account = accountRepository.findByStudentId(studentId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for student " + studentId));

        return BillingStatusResponse.builder()
                .studentId(account.getStudentId())
                .outstandingBalance(account.getOutstandingBalance())
                .chargeCount(account.getCharges().size())
                .paymentCount(account.getPayments().size())
                .build();
    }

    @Transactional
    public PaymentResponse processPayment(UUID studentId, PaymentRequest request) {
        BillingAccount account = accountRepository.findByStudentId(studentId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for student " + studentId));

        Payment payment = Payment.builder()
                .account(account)
                .amount(request.getAmount())
                .method(request.getMethod())
                .paymentDate(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        account.setOutstandingBalance(account.getOutstandingBalance().subtract(payment.getAmount()));
        accountRepository.save(account);

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .paymentDate(payment.getPaymentDate())
                .newBalance(account.getOutstandingBalance())
                .build();
    }

    @Transactional
    public TuitionResponse calculateTuition(UUID studentId) {
        log.info("Calculating tuition for student: {}", studentId);

        // 1. Synchronous call to Student Service to verify identity
        String studentUrl = studentServiceUrl + "/students/" + studentId;
        try {
            ResponseEntity<StudentResponse> studentResp = restTemplate.getForEntity(studentUrl, StudentResponse.class);
            if (studentResp.getStatusCode().isError() || studentResp.getBody() == null) {
                throw new RuntimeException("Invalid student status");
            }
        } catch (Exception e) {
            log.error("Failed to validate student: {}", e.getMessage());
            throw new RuntimeException("Student validation failed", e);
        }

        // 2. Synchronous call to Course Service to get enrolled courses
        String coursesUrl = courseServiceUrl + "/students/" + studentId + "/courses";
        List<EnrolledCourseResponse> courses;
        try {
            ResponseEntity<List<EnrolledCourseResponse>> courseResp = restTemplate.exchange(
                    coursesUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EnrolledCourseResponse>>() {}
            );
            courses = courseResp.getBody();
            if (courses == null) {
                throw new RuntimeException("Failed to retrieve courses");
            }
        } catch (Exception e) {
            log.error("Failed to retrieve courses: {}", e.getMessage());
            throw new RuntimeException("Course retrieval failed", e);
        }

        // 3. Calculate tuition
        int totalCredits = courses.stream().mapToInt(EnrolledCourseResponse::getCredits).sum();
        BigDecimal tuitionAmount = COST_PER_CREDIT.multiply(new BigDecimal(totalCredits));

        // 4. Create Charge
        AddChargeRequest chargeReq = AddChargeRequest.builder()
                .chargeType(ChargeType.TUITION)
                .amount(tuitionAmount)
                .description("Tuition fee for " + totalCredits + " credits")
                .build();

        ChargeResponse charge = addCharge(studentId, chargeReq);

        return TuitionResponse.builder()
                .chargeId(charge.getChargeId())
                .totalCredits(totalCredits)
                .tuitionAmount(tuitionAmount)
                .build();
    }

    private ChargeResponse mapToChargeResponse(Charge charge) {
        return ChargeResponse.builder()
                .chargeId(charge.getChargeId())
                .chargeType(charge.getChargeType())
                .amount(charge.getAmount())
                .description(charge.getDescription())
                .createdAt(charge.getCreatedAt())
                .build();
    }
}
