package com.unicampus.mealplan.dto;

import com.unicampus.mealplan.domain.Subscription;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class SubscriptionResponse {
    private UUID subscriptionId;
    private UUID planId;
    private String planName;
    private UUID studentId;
    private Date startDate;
    private Date endDate;
    private Subscription.SubscriptionStatus status;
}
