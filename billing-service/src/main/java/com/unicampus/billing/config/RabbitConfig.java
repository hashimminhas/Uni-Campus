package com.unicampus.billing.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String LIBRARY_EXCHANGE = "library.events";
    public static final String BILLING_EXCHANGE = "billing.events";
    
    public static final String BILLING_QUEUE = "billing.queue";
    
    @Bean
    public Queue billingQueue() {
        return new Queue(BILLING_QUEUE);
    }

    @Bean
    public TopicExchange libraryExchange() {
        return new TopicExchange(LIBRARY_EXCHANGE);
    }

    @Bean
    public TopicExchange billingExchange() {
        return new TopicExchange(BILLING_EXCHANGE);
    }

    @Bean
    public Binding bindLibraryOverdue(Queue billingQueue, TopicExchange libraryExchange) {
        return BindingBuilder.bind(billingQueue).to(libraryExchange).with("book.overdue");
    }

    @Bean
    public Binding bindHousingFee(Queue billingQueue, TopicExchange billingExchange) {
        return BindingBuilder.bind(billingQueue).to(billingExchange).with("housing.fee.charged");
    }

    @Bean
    public Binding bindMealplanFee(Queue billingQueue, TopicExchange billingExchange) {
        return BindingBuilder.bind(billingQueue).to(billingExchange).with("mealplan.fee.charged");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new org.springframework.amqp.support.converter.SimpleMessageConverter();
    }
}
