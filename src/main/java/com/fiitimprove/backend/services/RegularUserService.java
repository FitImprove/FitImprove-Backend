package com.fiitimprove.backend.services;


import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for managing operations related to RegularUser entities,
 * including creating users and retrieving all users.
 * Also initializes default user settings upon user creation.
 */
@Service
public class RegularUserService {
    @Autowired
    private RegularUserRepository regularUserRepository;
    @Autowired
    private SettingsService settingsService;

    /**
     * Creates and saves a new RegularUser, sets their join date to the current date,
     * and initializes their default settings.
     *
     * @param regularUser The RegularUser object to be created.
     * @return The saved RegularUser with the assigned ID and settings initialized.
     */
    public RegularUser createRegularUser(RegularUser regularUser) {
        regularUser.setJoinedAt(LocalDate.now());
        RegularUser savedUser = regularUserRepository.save(regularUser);

        Settings settings = new Settings();
        settings.setUser(savedUser);
        settings.setTheme(Settings.Theme.PURPLE);
        settings.setFontSize(12);
        settings.setNotifications(true);
        settingsService.createSettings(savedUser.getId(), settings);

        return savedUser;
    }

    /**
     * Retrieves a list of all RegularUser entities from the database.
     * @return A List containing all RegularUser objects.
     */
    public List<RegularUser> findAllRegularUsers() {
        return regularUserRepository.findAll();
    }
}
