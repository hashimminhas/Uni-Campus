package com.unicampus.library.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Only ADMIN can create or update/modify books in the catalog
                .requestMatchers(HttpMethod.POST, "/api/library/books").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/library/books/**").hasRole("ADMIN")
                
                // Allow anyone/students to browse the catalog, reserve loans, return books, or get loans
                .requestMatchers(HttpMethod.GET, "/api/library/books").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/library/books/**/borrow").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/library/loans/**/return").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/library/loans/student/**").permitAll()
                
                // Swagger documentation and health monitoring endpoints
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                
                // All other endpoints require valid JWT authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
