package com.fiitimprove.backend.services;


import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.repositories.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private SettingsService settingsService;

    public Coach createCoach(Coach coach) {
        coach.setJoinedAt(java.time.LocalDate.now());
        coach.setVerified(false);
        Coach savedCoach = coachRepository.save(coach);
        Settings settings = new Settings();
        settings.setUser(savedCoach);
        settings.setTheme(Settings.Theme.PURPLE);
        settings.setFontSize(12);
        settings.setNotifications(true);
        settingsService.createSettings(savedCoach.getId(), settings);

        return savedCoach;
    }
    public List<Coach> findAllCoaches() {
        return coachRepository.findAll();
    }


}