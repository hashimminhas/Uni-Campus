package com.unicampus.mealplan.service;

import com.unicampus.mealplan.domain.MealPlan;
import com.unicampus.mealplan.domain.Subscription;
import com.unicampus.mealplan.dto.CreateMealPlanRequest;
import com.unicampus.mealplan.dto.SubscriptionResponse;
import com.unicampus.mealplan.repository.MealPlanRepository;
import com.unicampus.mealplan.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final StudentServiceClient studentServiceClient;
    private final MealPlanEventPublisher eventPublisher;

    public MealPlan createPlan(CreateMealPlanRequest request) {
        MealPlan plan = MealPlan.builder()
                .planId(UUID.randomUUID())
                .name(request.getName())
                .mealsPerWeek(request.getMealsPerWeek())
                .price(request.getPrice())
                .semester(request.getSemester())
                .isActive(true)
                .build();
        return mealPlanRepository.save(plan);
    }

    public List<MealPlan> getAllPlans() {
        return mealPlanRepository.findAll();
    }

    public Optional<MealPlan> getPlanById(UUID planId) {
        return mealPlanRepository.findById(planId);
    }

    @Transactional
    public Subscription subscribe(UUID planId, UUID studentId) {
        // 1. Check eligibility
        if (!isEligible(studentId)) {
            throw new IllegalStateException("Student is not eligible for a meal plan subscription");
        }

        // 2. Validate plan
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));
        
        if (!plan.getIsActive()) {
            throw new IllegalStateException("Meal plan is not active");
        }

        // 3. Create subscription
        Subscription subscription = Subscription.builder()
                .subscriptionId(UUID.randomUUID())
                .planId(planId)
                .studentId(studentId)
                .startDate(new Date())
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .build();
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // 4. Publish event
        Map<String, Object> event = new HashMap<>();
        event.put("subscriptionId", savedSubscription.getSubscriptionId().toString());
        event.put("studentId", studentId.toString());
        event.put("planId", planId.toString());
        event.put("amount", plan.getPrice().toString());
        event.put("semester", plan.getSemester());
        event.put("timestamp", new Date());

        eventPublisher.publishMealPlanSubscribed(event);

        return savedSubscription;
    }

    @Transactional
    public void cancelSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        
        if (subscription.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Subscription is already " + subscription.getStatus());
        }

        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscription.setEndDate(new Date());
        subscriptionRepository.save(subscription);

        // Publish event
        Map<String, Object> event = new HashMap<>();
        event.put("subscriptionId", subscriptionId);
        event.put("studentId", subscription.getStudentId());
        event.put("timestamp", new Date());

        eventPublisher.publishMealPlanCancelled(event);
    }

    public boolean isEligible(UUID studentId) {
        // Must be active student
        boolean isActiveStudent = studentServiceClient.isStudentActive(studentId);
        if (!isActiveStudent) return false;

        // Must not have an active subscription
        return subscriptionRepository.findByStudentIdAndStatus(studentId, Subscription.SubscriptionStatus.ACTIVE).isEmpty();
    }

    public List<SubscriptionResponse> getSubscriptionsForStudent(UUID studentId) {
        return subscriptionRepository.findByStudentId(studentId).stream()
                .map(sub -> {
                    String planName = mealPlanRepository.findById(sub.getPlanId())
                            .map(MealPlan::getName)
                            .orElse("Unknown Plan");
                    return SubscriptionResponse.builder()
                            .subscriptionId(sub.getSubscriptionId())
                            .planId(sub.getPlanId())
                            .planName(planName)
                            .studentId(sub.getStudentId())
                            .startDate(sub.getStartDate())
                            .endDate(sub.getEndDate())
                            .status(sub.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public long getPlanPopularity(UUID planId) {
        return subscriptionRepository.countByPlanIdAndStatus(planId, Subscription.SubscriptionStatus.ACTIVE);
    }

    @Transactional
    public MealPlan togglePlanStatus(UUID planId) {
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));
        plan.setIsActive(!plan.getIsActive());
        return mealPlanRepository.save(plan);
    }
}
