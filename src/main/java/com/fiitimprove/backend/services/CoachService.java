package com.fiitimprove.backend.services;


import com.fiitimprove.backend.dto.CoachSearchDTO;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.LevenshteinDistance;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private SettingsService settingsService;

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
    public List<Coach> findAllCoaches() {
        return coachRepository.findAll();
    }

    public List<Coach> search(CoachSearchDTO data) {
        List<Coach> allCoaches = coachRepository.findAll();
        LevenshteinDistance levenshtein = new LevenshteinDistance();

        var a = allCoaches.stream()
                        .filter(coach -> (data.getName() == null || levenshtein.apply(coach.getName().toLowerCase() + " " + coach.getSurname().toLowerCase(), data.getName().toLowerCase()) < 3) 
                                        && (data.getGender() == null || coach.getGender().equals(data.getGender()))
                                        && (data.getField() == null || coach.getFields().contains(data.getField())))
                        .collect(Collectors.toList());
        if (a.size() == 0) {
            a = allCoaches.stream()
                        .filter(coach -> (data.getName() == null 
                                            || levenshtein.apply(coach.getName().toLowerCase(), data.getName().toLowerCase()) < 2 
                                            || levenshtein.apply(coach.getSurname().toLowerCase(), data.getName().toLowerCase()) < 2) 
                                        && (data.getGender() == null || coach.getGender().equals(data.getGender()))
                                        && (data.getField() == null || coach.getFields().contains(data.getField())))
                        .collect(Collectors.toList());
        }
        return a;
    }
}