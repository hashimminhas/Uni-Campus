package com.unicampus.billing.controller;

import com.unicampus.billing.dto.*;
import com.unicampus.billing.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/billing")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Billing Service APIs")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/{studentId}/charges")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "View aggregated charges")
    public List<ChargeResponse> getCharges(@PathVariable UUID studentId) {
        requireStudentOrAdmin(studentId);
        return billingService.getCharges(studentId);
    }

    @PostMapping("/{studentId}/tuition")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Calculate tuition from enrolled credits")
    public TuitionResponse calculateTuition(@PathVariable UUID studentId) {
        requireStudentOrAdmin(studentId);
        return billingService.calculateTuition(studentId);
    }

    @GetMapping("/{studentId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "View payment status and outstanding balance")
    public BillingStatusResponse getStatus(@PathVariable UUID studentId) {
        requireStudentOrAdmin(studentId);
        return billingService.getStatus(studentId);
    }

    @PostMapping("/{studentId}/pay")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Simulate payment")
    public PaymentResponse pay(@PathVariable UUID studentId, @Valid @RequestBody PaymentRequest request) {
        requireStudentOrAdmin(studentId);
        return billingService.processPayment(studentId, request);
    }

    @PostMapping("/{studentId}/charges")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a manual charge")
    public ChargeResponse addCharge(@PathVariable UUID studentId, @Valid @RequestBody AddChargeRequest request) {
        requireStudentOrAdmin(studentId);
        return billingService.addCharge(studentId, request);
    }

    private void requireStudentOrAdmin(UUID studentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            return;
        }
        String principal = String.valueOf(auth.getPrincipal());
        if (!studentId.toString().equals(principal)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }
}
