package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.UserUpdateProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
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
        user.hashPassword(passwordEncoder);
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
    public User updateUser(Long userId, UserUpdateProfileRequest updateRequest) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        // Оновлення полів
        if (updateRequest.getName() != null) {
            existingUser.setName(updateRequest.getName());
        }
        if (updateRequest.getSurname() != null) {
            existingUser.setSurname(updateRequest.getSurname());
        }
        if (updateRequest.getUsername() != null) {
            if (userRepository.findByUsername(updateRequest.getUsername()).isPresent() &&
                    !updateRequest.getUsername().equals(existingUser.getUsername())) {
                throw new IllegalArgumentException("Username " + updateRequest.getUsername() + " is already taken");
            }
            existingUser.setUsername(updateRequest.getUsername());
        }
        if (updateRequest.getEmail() != null) {
            if (userRepository.findByEmail(updateRequest.getEmail()).isPresent() &&
                    !updateRequest.getEmail().equals(existingUser.getEmail())) {
                throw new IllegalArgumentException("Email " + updateRequest.getEmail() + " is already taken");
            }
            existingUser.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            existingUser.setPassword(updateRequest.getPassword());
            existingUser.hashPassword(passwordEncoder);
        }
        if (updateRequest.getGender() != null) {
            existingUser.setGender(updateRequest.getGender());
        }
        if (updateRequest.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateRequest.getDateOfBirth());
        }
        if (updateRequest.getLinks() != null) {
            existingUser.setLinks(updateRequest.getLinks());
        }
        if (updateRequest.getSelfInformation() != null) {
            existingUser.setSelfInformation(updateRequest.getSelfInformation());
        }

        // Оновлення полів, специфічних для Coach
        if (existingUser instanceof Coach coach) {
            if (updateRequest.getFields() != null) {
                coach.setFields(updateRequest.getFields());
            }
            if (updateRequest.getSkills() != null) {
                coach.setSkills(updateRequest.getSkills());
            }
            if (updateRequest.getSelfIntroduction() != null) {
                coach.setSelfIntroduction(updateRequest.getSelfIntroduction());
            }
            if (updateRequest.getWorksInFieldSince() != null) {
                coach.setWorksInFieldSince(updateRequest.getWorksInFieldSince());
            }
        } else if (existingUser instanceof RegularUser) {
            if (updateRequest.getFields() != null || updateRequest.getSkills() != null ||
                    updateRequest.getSelfIntroduction() != null || updateRequest.getWorksInFieldSince() != null) {
                throw new IllegalArgumentException("RegularUser cannot update Coach-specific fields (fields, skills, selfIntroduction, worksInFieldSince)");
            }
        }

        return userRepository.save(existingUser);
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }
}