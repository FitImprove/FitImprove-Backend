package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.SettingsRepository;
import com.fiitimprove.backend.repositories.UserRepository;
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
        return settingsRepository.findByUserId(userId);
    }

    public List<Settings> findAll() {
        return settingsRepository.findAll();
    }
}