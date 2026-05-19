package com.unicampus.mealplan.controller;

import com.unicampus.mealplan.domain.MealPlan;
import com.unicampus.mealplan.domain.Subscription;
import com.unicampus.mealplan.dto.CreateMealPlanRequest;
import com.unicampus.mealplan.dto.SubscribeRequest;
import com.unicampus.mealplan.dto.SubscriptionResponse;
import com.unicampus.mealplan.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meal-plan")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @PostMapping("/plans")
    public ResponseEntity<MealPlan> createPlan(@RequestBody CreateMealPlanRequest request) {
        return new ResponseEntity<>(mealPlanService.createPlan(request), HttpStatus.CREATED);
    }

    @GetMapping("/plans")
    public ResponseEntity<List<MealPlan>> getAllPlans() {
        return ResponseEntity.ok(mealPlanService.getAllPlans());
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<MealPlan> getPlanById(@PathVariable UUID planId) {
        return mealPlanService.getPlanById(planId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/plans/{planId}/subscribe")
    public ResponseEntity<?> subscribe(@PathVariable UUID planId, @RequestBody SubscribeRequest request) {
        try {
            Subscription subscription = mealPlanService.subscribe(planId, request.getStudentId());
            return new ResponseEntity<>(subscription, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/subscriptions/student/{studentId}")
    public ResponseEntity<List<SubscriptionResponse>> getStudentSubscriptions(@PathVariable UUID studentId) {
        return ResponseEntity.ok(mealPlanService.getSubscriptionsForStudent(studentId));
    }

    @DeleteMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<Void> cancelSubscription(@PathVariable UUID subscriptionId) {
        try {
            mealPlanService.cancelSubscription(subscriptionId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/students/{studentId}/eligibility")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable UUID studentId) {
        return ResponseEntity.ok(mealPlanService.isEligible(studentId));
    }

    @GetMapping("/plans/{planId}/popularity")
    public ResponseEntity<Long> getPlanPopularity(@PathVariable UUID planId) {
        return ResponseEntity.ok(mealPlanService.getPlanPopularity(planId));
    }

    @PatchMapping("/plans/{planId}/toggle")
    public ResponseEntity<MealPlan> togglePlanStatus(@PathVariable UUID planId) {
        try {
            return ResponseEntity.ok(mealPlanService.togglePlanStatus(planId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
