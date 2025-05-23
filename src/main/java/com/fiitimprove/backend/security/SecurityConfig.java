package com.fiitimprove.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for setting up Spring Security in the application.
 * It configures endpoint access permissions, session management, CSRF protection,
 * and integrates JWT authentication through a custom filter.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests. Specifies which endpoints are allowed to be accessed with and without authentication
     * @param http 
     * @param jwtAuthenticationFilter the custom JWT filter to validate JWT tokens
     * @return a configured
     * @throws Exception if an error occurs while building the security chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/users/signup",
                                "/api/password/**",
                                "/api/users/signIn",
                                "api/users/getAll",
                                "/ws/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/gyms/update",
                                "/api/gyms/delete/{coach_id}",
                                "/api/users/user",
                                "/api/users/update",
                                "/api/chats/create",
                                "/api/chats/coach",
                                "/api/chats/user",
                                "/api/chats/{chatId}",
                                "/api/users/notifications",
                                "/api/images/files",
                                "/api/images/upload",
                                "/api/images/descriptors",
                                "/api/images/descriptors/{userId}",
                                "/api/images/get/{filename}",
                                "/api/images/del/{imgId}",
                                "/api/notification/**",
                                "/api/training-users/**",
                                "/api/trainings/**",
                                "/api/users/{userId}",
                                "/api/settings/**"
                        ).authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }



}
