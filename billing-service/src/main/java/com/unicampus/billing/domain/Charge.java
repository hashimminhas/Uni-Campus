package com.unicampus.billing.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "charges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID chargeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BillingAccount account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChargeType chargeType;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
