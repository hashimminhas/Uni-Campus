package com.unicampus.exam.config;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange examEventsExchange() {
        return new TopicExchange("exam.events", true, false);
    }

    @Bean
    public Declarables examEventBindings(TopicExchange examEventsExchange) {
        return new Declarables(examEventsExchange);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
