package com.unicampus.billing.controller;

import com.unicampus.billing.dto.*;
import com.unicampus.billing.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Billing Service APIs")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/{studentId}/charges")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "View aggregated charges")
    public List<ChargeResponse> getCharges(@PathVariable UUID studentId) {
        return billingService.getCharges(studentId);
    }

    @PostMapping("/{studentId}/tuition")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Calculate tuition from enrolled credits")
    public TuitionResponse calculateTuition(@PathVariable UUID studentId) {
        return billingService.calculateTuition(studentId);
    }

    @GetMapping("/{studentId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "View payment status and outstanding balance")
    public BillingStatusResponse getStatus(@PathVariable UUID studentId) {
        return billingService.getStatus(studentId);
    }

    @PostMapping("/{studentId}/pay")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Simulate payment")
    public PaymentResponse pay(@PathVariable UUID studentId, @Valid @RequestBody PaymentRequest request) {
        return billingService.processPayment(studentId, request);
    }

    @PostMapping("/{studentId}/charges")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a manual charge")
    public ChargeResponse addCharge(@PathVariable UUID studentId, @Valid @RequestBody AddChargeRequest request) {
        return billingService.addCharge(studentId, request);
    }
}
