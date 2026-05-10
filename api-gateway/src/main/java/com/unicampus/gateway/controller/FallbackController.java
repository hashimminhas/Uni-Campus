package com.unicampus.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Fallback endpoint invoked by the circuit breaker when an upstream service is unavailable.
 */
@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<Map<String, Object>> fallback() {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "error", "Service Unavailable",
                "message", "The upstream service is temporarily unavailable. Please try again later.",
                "timestamp", Instant.now().toString()
        ));
    }
}
