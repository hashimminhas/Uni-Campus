package com.unicampus.mealplan.repository;

import com.unicampus.mealplan.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByStudentIdAndStatus(UUID studentId, Subscription.SubscriptionStatus status);
    List<Subscription> findByStudentId(UUID studentId);
    long countByPlanIdAndStatus(UUID planId, Subscription.SubscriptionStatus status);
}
