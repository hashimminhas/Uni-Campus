package com.unicampus.mealplan.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "meal_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlan {
    @Id
    private UUID planId;

    private String name;
    private Integer mealsPerWeek;
    private BigDecimal price;
    private String semester;
    private Boolean isActive;
}
