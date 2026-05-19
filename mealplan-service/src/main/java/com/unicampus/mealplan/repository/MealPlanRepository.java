package com.unicampus.mealplan.repository;

import com.unicampus.mealplan.domain.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {
    List<MealPlan> findByIsActiveTrue();
}
