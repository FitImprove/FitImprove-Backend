package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class allows to manage coach related operations like creation and search
 */
@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private SettingsService settingsService;

    /**
     * Creates and saves a new {@link Coach}, and initializes their default {@link Settings}.
     *
     * @param coach the coach object to create
     * @return the saved coach object
     */
    public Coach createCoach(Coach coach) {
        coach.setJoinedAt(java.time.LocalDate.now());

        Coach savedCoach = coachRepository.save(coach);
        Settings settings = new Settings();
        settings.setUser(savedCoach);
        settings.setTheme(Settings.Theme.PURPLE);
        settings.setFontSize(12);
        settings.setNotifications(true);
        settingsService.createSettings(savedCoach.getId(), settings);

        return savedCoach;
    }

    /**
     * Retrieves all {@link Coach} entities from the database.
     *
     * @return a list of all coaches
     */
    public List<Coach> findAllCoaches() {
        return coachRepository.findAll();
    }
    
    /**
     * Searches for coaches based on optional criteria: name, field of expertise, and gender.
     * <p>
     * If a parameter is {@code null} or blank (for Strings), it is ignored in the filtering.
     *
     * @param name   part or full name of the coach (case-insensitive)
     * @param field  field of expertise (case-insensitive)
     * @param gender gender of the coach
     * @return a filtered list of coaches that match the criteria
     */
    public List<Coach> search(String name, String field, User.Gender gender) {
        List<Coach> allCoaches = coachRepository.findAll();

        return allCoaches.stream()
            .filter(coach -> {
                boolean matchesName = true;
                if (name != null && !name.isBlank()) {
                    String fullName = (coach.getName() + " " + coach.getSurname()).toLowerCase();
                    matchesName = fullName.contains(name.toLowerCase());
                }
                System.out.printf("For name: %s, %s, the match: %b", name, (coach.getName() + " " + coach.getSurname()).toLowerCase(), matchesName);

                boolean matchesField = true;
                if (field != null && !field.isBlank()) {
                    matchesField = coach.getFields() != null &&
                                   coach.getFields().stream()
                                        .anyMatch(f -> f.equalsIgnoreCase(field));
                }

                boolean matchesGender = (gender == null) || gender == coach.getGender();

                return matchesName && matchesField && matchesGender;
            })
            .collect(Collectors.toList());
    }
}