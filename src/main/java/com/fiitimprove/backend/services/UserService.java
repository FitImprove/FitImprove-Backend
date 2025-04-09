package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SettingsService settingsService;
    private boolean emailExists(String email) {
        return  userRepository.findByEmail(email).isPresent();
    }
    public User signup(User user) {
        System.out.println(user.getRole() + user.getName());
        if (!(user instanceof Coach) && !(user instanceof RegularUser)) {
            throw new IllegalArgumentException("User must be of type Coach or RegularUser");
        }
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("This email has already exist");
        }
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
    public List<User> findAll() {
        return userRepository.findAll();
    }


}