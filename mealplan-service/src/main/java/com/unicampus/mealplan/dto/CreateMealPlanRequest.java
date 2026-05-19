package com.unicampus.mealplan.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateMealPlanRequest {
    private String name;
    private Integer mealsPerWeek;
    private BigDecimal price;
    private String semester;
}
