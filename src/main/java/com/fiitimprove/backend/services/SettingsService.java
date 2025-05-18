package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.SettingsRepository;
import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.SettingsUpdateRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing user settings,
 * including creation, retrieval, update, and listing all settings.
 */
@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates and saves a Settings entity associated with the user identified by userId.
     *
     * @param userId   The ID of the user to whom the settings belong.
     * @param settings The Settings object to be created and saved.
     * @return The saved Settings entity.
     * @throws RuntimeException if the user with the given userId is not found.
     */
    public Settings createSettings(Long userId, Settings settings) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        settings.setUser(user);
        return settingsRepository.save(settings);
    }

    /**
     * Retrieves the Settings entity associated with the specified user ID.
     *
     * @param userId The ID of the user whose settings are to be retrieved.
     * @return The Settings entity for the given user.
     * @throws java.util.NoSuchElementException if settings for the user are not found.
     */
    public Settings findByUserId(Long userId) {
        return settingsRepository.findByUserId(userId).get();
    }

    /**
     * Updates the Settings of a user based on the provided update request.
     *
     * @param userId  The ID of the user whose settings are to be updated.
     * @param request The SettingsUpdateRequest containing new settings values.
     * @return The updated Settings entity after saving.
     * @throws java.util.NoSuchElementException if settings for the user are not found.
     */
    public Settings updateSettings(Long userId, SettingsUpdateRequest request) {
        System.out.println("tuuuu");
        Optional<Settings> set = settingsRepository.findByUserId(userId);
        Settings settings= null;
        if(set.isPresent()) {
            settings = set.get();
        }
        assert settings != null;
        settings.setTheme(request.getTheme());
        settings.setFontSize(request.getFontSize());
        settings.setNotifications(request.getNotifications());
        System.out.println("tu uz" + settings.getUser());
        System.out.println("Is managed: " + entityManager.contains(settings));
        Settings s = settingsRepository.save(settings);
        return s;
    }

    /**
     * Retrieves all Settings entities from the database.
     *
     * @return A List containing all Settings.
     */
    public List<Settings> findAll() {
        return settingsRepository.findAll();
    }
}