package com.unicampus.billing.dto;

import com.unicampus.billing.domain.ChargeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeResponse {
    private UUID chargeId;
    private ChargeType chargeType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}
