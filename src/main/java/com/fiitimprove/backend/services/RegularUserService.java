package com.fiitimprove.backend.services;


import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegularUserService {

    @Autowired
    private RegularUserRepository regularUserRepository;
    @Autowired
    private SettingsService settingsService;
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
    public List<RegularUser> findAllRegularUsers() {
        return regularUserRepository.findAll();
    }


}
