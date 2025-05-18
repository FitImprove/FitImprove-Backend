package com.fiitimprove.backend.security;

import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Configuration class that defines a {@link UserDetailsService} bean used by Spring Security
 * for authentication and user lookup.
 */
@Configuration
public class UserDetailsConfig {
    @Autowired
    private UserRepository userRepository;

    /**
     * Defines a {@link UserDetailsService} bean that retrieves user details based on a provided username.
     * <p>
     * This method uses the {@link UserRepository} to fetch the {@link User} entity and converts it
     * to a Spring Security {@link org.springframework.security.core.userdetails.User} object with default role "USER".
     *
     * @return a {@link UserDetailsService} that can be used by Spring Security to load user-specific data
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities("USER")
                    .build();
        };
    }
}