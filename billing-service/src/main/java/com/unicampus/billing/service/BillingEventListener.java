package com.unicampus.billing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicampus.billing.config.RabbitConfig;
import com.unicampus.billing.domain.ChargeType;
import com.unicampus.billing.dto.AddChargeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingEventListener {

    private final BillingService billingService;
    private final ObjectMapper objectMapper;

    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("2.50");

    @RabbitListener(queues = RabbitConfig.BILLING_QUEUE)
    public void handleEvent(String messageJson) {
        log.info("Received billing event: {}", messageJson);
        try {
            JsonNode node = objectMapper.readTree(messageJson);
            
            if (node.has("daysOverdue") && node.has("bookId")) {
                UUID studentId = UUID.fromString(node.get("studentId").asText());
                long daysOverdue = node.get("daysOverdue").asLong();
                String bookTitle = node.has("bookTitle") ? node.get("bookTitle").asText() : "Unknown Book";
                
                BigDecimal amount = DAILY_FINE_RATE.multiply(new BigDecimal(daysOverdue));
                AddChargeRequest charge = AddChargeRequest.builder()
                        .chargeType(ChargeType.FINE)
                        .amount(amount)
                        .description("Overdue fine for book: " + bookTitle + " (" + daysOverdue + " days)")
                        .build();
                billingService.addCharge(studentId, charge);
                log.info("Processed overdue fine for student {}", studentId);
                
            } else if (node.has("roomId") && node.has("semester")) {
                UUID studentId = UUID.fromString(node.get("studentId").asText());
                BigDecimal amount = new BigDecimal(node.get("amount").asText());
                String semester = node.get("semester").asText();
                
                AddChargeRequest charge = AddChargeRequest.builder()
                        .chargeType(ChargeType.HOUSING)
                        .amount(amount)
                        .description("Housing fee for semester: " + semester)
                        .build();
                billingService.addCharge(studentId, charge);
                log.info("Processed housing fee for student {}", studentId);
                
            } else if (node.has("planId") && node.has("semester")) {
                UUID studentId = UUID.fromString(node.get("studentId").asText());
                BigDecimal amount = new BigDecimal(node.get("amount").asText());
                String semester = node.get("semester").asText();
                
                AddChargeRequest charge = AddChargeRequest.builder()
                        .chargeType(ChargeType.MEALPLAN)
                        .amount(amount)
                        .description("Meal plan fee for semester: " + semester)
                        .build();
                billingService.addCharge(studentId, charge);
                log.info("Processed meal plan fee for student {}", studentId);
            } else {
                log.warn("Unrecognized event structure: {}", messageJson);
            }
        } catch (Exception e) {
            log.error("Error processing billing event", e);
        }
    }
}
