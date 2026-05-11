package com.unicampus.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TuitionResponse {
    private UUID chargeId;
    private int totalCredits;
    private BigDecimal tuitionAmount;
}
