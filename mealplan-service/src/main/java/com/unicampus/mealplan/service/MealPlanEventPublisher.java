package com.unicampus.mealplan.service;

import com.unicampus.mealplan.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishMealPlanSubscribed(Map<String, Object> event) {
        log.info("Publishing mealplan.subscribed event: {}", event);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "mealplan.subscribed", event);
    }

    public void publishMealPlanCancelled(Map<String, Object> event) {
        log.info("Publishing mealplan.cancelled event: {}", event);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "mealplan.cancelled", event);
    }
}
