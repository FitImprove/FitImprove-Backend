package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.SettingsRepository;
import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.SettingsUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

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
        Settings settings = settingsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found for user with id: " + userId));
        settings.setTheme(request.getTheme());
        settings.setFontSize(request.getFontSize());
        settings.setNotifications(request.getNotifications());
        return settingsRepository.save(settings);
    }
    public List<Settings> findAll() {
        return settingsRepository.findAll();
    }
}