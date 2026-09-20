package com.flowai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TEMPORARY Security Configuration — Phase 1 Only.
 *
 * IMPORTANT: This configuration permits ALL requests.
 * This is ONLY acceptable during development bootstrapping.
 * Phase 2 will replace this entirely with JWT-based security.
 *
 * DO NOT USE THIS IN PRODUCTION.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // ← TEMPORARY. Phase 2 will restrict this.
                );

        return http.build();
    }
}