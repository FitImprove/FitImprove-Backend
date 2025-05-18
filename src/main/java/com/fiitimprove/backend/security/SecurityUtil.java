package com.fiitimprove.backend.security;

import com.fiitimprove.backend.exceptions.AccessDeniedException;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class for retrieving information about the currently authenticated user.
 * This class provides helper methods to get the full {@link User} entity or just the user ID.
 */
@Component
public class SecurityUtil {
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code SecurityUtil} with the provided {@link UserRepository}.
     *
     * @param userRepository the repository used to fetch user information from the database
     */
    public SecurityUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the currently authenticated {@link User} from the security context.
     * <p>
     * If the user is not authenticated or the authentication principal is invalid,
     * an {@link AccessDeniedException} is thrown.
     * If the user cannot be found in the repository, a {@link ResourceNotFoundException} is thrown.
     *
     * @return the authenticated {@link User}
     * @throws AccessDeniedException       if the user is not authenticated
     * @throws ResourceNotFoundException   if the user does not exist in the database
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("User is not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * @return the authenticated user's ID
     * @throws AccessDeniedException       if the user is not authenticated
     * @throws ResourceNotFoundException   if the user does not exist in the database
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}