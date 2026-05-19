package com.unicampus.mealplan.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SubscribeRequest {
    private UUID studentId;
}
