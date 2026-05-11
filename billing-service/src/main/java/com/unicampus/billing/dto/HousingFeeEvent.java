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
public class HousingFeeEvent {
    private UUID studentId;
    private UUID roomId;
    private String semester;
    private BigDecimal amount;
    private String chargeType;
}
