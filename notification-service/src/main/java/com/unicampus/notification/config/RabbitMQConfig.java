package com.unicampus.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COURSE_EVENTS_EXCHANGE  = "course.events";
    public static final String EXAM_EVENTS_EXCHANGE    = "exam.events";
    public static final String LIBRARY_EVENTS_EXCHANGE = "library.events";

    public static final String NOTIFICATION_COURSE_QUEUE  = "notification.course.queue";
    public static final String NOTIFICATION_EXAM_QUEUE    = "notification.exam.queue";
    public static final String NOTIFICATION_LIBRARY_QUEUE = "notification.library.queue";

    public static final String ENROLLMENT_CONFIRMED_KEY = "enrollment.confirmed";
    public static final String COURSE_DROPPED_KEY       = "course.dropped";
    public static final String EXAM_SCHEDULED_KEY       = "exam.scheduled";
    public static final String BOOK_OVERDUE_KEY         = "book.overdue";

    // Exchanges

    @Bean
    public TopicExchange courseEventsExchange() {
        return new TopicExchange(COURSE_EVENTS_EXCHANGE);
    }

    @Bean
    public TopicExchange examEventsExchange() {
        return new TopicExchange(EXAM_EVENTS_EXCHANGE);
    }

    @Bean
    public TopicExchange libraryEventsExchange() {
        return new TopicExchange(LIBRARY_EVENTS_EXCHANGE);
    }

    // Queues

    @Bean
    public Queue notificationCourseQueue() {
        return new Queue(NOTIFICATION_COURSE_QUEUE, true);
    }

    @Bean
    public Queue notificationExamQueue() {
        return new Queue(NOTIFICATION_EXAM_QUEUE, true);
    }

    @Bean
    public Queue notificationLibraryQueue() {
        return new Queue(NOTIFICATION_LIBRARY_QUEUE, true);
    }

    // Bindings

    @Bean
    public Binding enrollmentConfirmedBinding() {
        return BindingBuilder
                .bind(notificationCourseQueue())
                .to(courseEventsExchange())
                .with(ENROLLMENT_CONFIRMED_KEY);
    }

    @Bean
    public Binding courseDroppedBinding() {
        return BindingBuilder
                .bind(notificationCourseQueue())
                .to(courseEventsExchange())
                .with(COURSE_DROPPED_KEY);
    }

    @Bean
    public Binding examScheduledBinding() {
        return BindingBuilder
                .bind(notificationExamQueue())
                .to(examEventsExchange())
                .with(EXAM_SCHEDULED_KEY);
    }

    @Bean
    public Binding bookOverdueBinding() {
        return BindingBuilder
                .bind(notificationLibraryQueue())
                .to(libraryEventsExchange())
                .with(BOOK_OVERDUE_KEY);
    }
}
