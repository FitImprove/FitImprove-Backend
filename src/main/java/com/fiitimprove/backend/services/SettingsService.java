package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.SettingsRepository;
import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.SettingsUpdateRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    public Settings createSettings(Long userId, Settings settings) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        settings.setUser(user);
        return settingsRepository.save(settings);
    }

    public Settings findByUserId(Long userId) {
        return settingsRepository.findByUserId(userId).get();
    }
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
    public List<Settings> findAll() {
        return settingsRepository.findAll();
    }
}