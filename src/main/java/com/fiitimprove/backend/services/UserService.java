package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.IncorrectDataException;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.NotificationUpdateRequest;
import com.fiitimprove.backend.requests.UserUpdateProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing users including registration, updating profiles, and notification settings.
 * Supports both {@link Coach} and {@link RegularUser} user types.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Checks if an email is already registered in the system.
     *
     * @param email the email address to check
     * @return true if the email already exists, false otherwise
     */
    private boolean emailExists(String email) {
        return  userRepository.findByEmail(email).isPresent();
    }

    /**
     * Registers a new user (Coach or RegularUser) with default settings.
     * Password is hashed before saving.
     *
     * @param user the user to register
     * @return the saved user with assigned settings
     * @throws IncorrectDataException if user type is invalid or email already exists
     */
    @Transactional
    public User signup(User user) {
        if (!(user instanceof Coach) && !(user instanceof RegularUser)) {
            throw new IncorrectDataException("User must be of type Coach or RegularUser");
        }
        if (emailExists(user.getEmail())) {
            throw new IncorrectDataException("This email has already exist");
        }
        user.hashPassword(passwordEncoder);
        User u = userRepository.save(user);
        Settings settings = new Settings();
        settings.setUser(u);
        settings.setTheme(Settings.Theme.PURPLE);
        settings.setFontSize(12);
        settings.setNotifications(true);
        settingsService.createSettings(u.getId(), settings);
        u.setSettings(settings);
        u = userRepository.save(u);
        return u;
    }

    /**
     * Updates an existing user's profile information.
     * Supports general user fields and additional fields for Coaches.
     *
     * @param userId        the ID of the user to update
     * @param updateRequest the profile update request containing new field values
     * @return the updated user entity
     * @throws Exception if user not found or if username is already taken by another user
     */
    public User updateUser(Long userId, UserUpdateProfileRequest updateRequest) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        if (updateRequest.getName() != null) {
            existingUser.setName(updateRequest.getName());
        }
        if (updateRequest.getSurname() != null) {
            existingUser.setSurname(updateRequest.getSurname());
        }
        if (updateRequest.getUsername() != null) {
            if (userRepository.findByUsername(updateRequest.getUsername()).isPresent() &&
                    !updateRequest.getUsername().equals(existingUser.getUsername())) {
                throw new IllegalArgumentException("Username " + updateRequest.getUsername() + " is already taken");
            }
            existingUser.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getGender() != null) {
            existingUser.setGender(updateRequest.getGender());
        }
        if (updateRequest.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateRequest.getDateOfBirth());
        }
        if (updateRequest.getLinks() != null) {
            existingUser.setLinks(updateRequest.getLinks());
        }
        if (updateRequest.getSelfInformation() != null) {
            existingUser.setSelfInformation(updateRequest.getSelfInformation());
        }
        if (existingUser instanceof Coach coach) {
            if (updateRequest.getFields() != null) {
                coach.setFields(updateRequest.getFields());
            }
            if (updateRequest.getSkills() != null) {
                coach.setSkills(updateRequest.getSkills());
            }
            if (updateRequest.getSelfIntroduction() != null) {
                coach.setSelfIntroduction(updateRequest.getSelfIntroduction());
            }
            if (updateRequest.getWorksInFieldSince() != null) {
                coach.setWorksInFieldSince(updateRequest.getWorksInFieldSince());
            }
        }
//        if (updateRequest.getSettings() != null) {
//            SettingsUpdateRequest settingsRequest = new SettingsUpdateRequest();
//            settingsRequest.setTheme(updateRequest.getSettings().getTheme());
//            settingsRequest.setFontSize(updateRequest.getSettings().getFontSize());
//            settingsRequest.setNotifications(updateRequest.getSettings().getNotifications());
//            settingsService.updateSettings(userId, settingsRequest);
//            Settings saved = settingsRepository.findByUserId(userId).orElseThrow();
//            System.out.println("AFTER SAVE: " + saved);
//        }
        System.out.println("tu"+existingUser);
        return userRepository.save(existingUser);
    }

    /**
     * Updates the notification settings for a user.
     * Enables or disables push notifications and updates the push token accordingly.
     *
     * @param userId  the ID of the user to update
     * @param request the notification update request containing push token and enable status
     * @throws IllegalArgumentException if the user does not exist
     */
    @Transactional
    public void updateNotifications(Long userId, NotificationUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        user.setPushtoken(request.isNotificationsEnabled() ? request.getExpoPushToken() : null);
        userRepository.save(user);
    }

    /**
     * Finds a user by their unique ID.
     *
     * @param userId the user ID
     * @return the found user
     * @throws IllegalArgumentException if the user does not exist
     */
    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
    }

    /**
     * Retrieves all users from the system.
     *
     * @return list of all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
}