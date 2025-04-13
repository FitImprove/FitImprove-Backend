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
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Без сесій
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/users/signup",
                                "/api/password/**",
                                "/api/images/get/{filename}",
                                "/api/images/files/{userId}",
                                "/api/images/descriptors/{userId}",
                                "/api/users/signIn",
                                "api/users/getAll",
                                "/api/password/recover/{email}",
                                "/api/password/check-code/{token}",
                                "/api/password/set-new-password"
                        ).permitAll()
                        .requestMatchers(
                                "/api/settings/update/{user_id}",
                                "/api/gyms/update/{coach_id}",
                                "/api/gyms/delete/{coach_id}",
                                "/api/users/user",
                                "/api/users/update/{id}",
                                "/api/chats/create",
                                "/api/chats/**",
                                "/api/chats/coach/{coach_id}",
                                "/api/chats/user/{regularUserId}",
                                "/api/images/upload/{user_id}",
                                "/api/trainings/**",
                                "/api/training-users/**"
                        ).authenticated()
                        .anyRequest().permitAll()
                        //.anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }



}
