package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.services.SettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Settings> createSettings(@PathVariable Long userId, @Valid @RequestBody Settings settings) {
        return ResponseEntity.ok(settingsService.createSettings(userId, settings));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Settings> getSettingsByUserId(@PathVariable Long userId) {
        Settings settings = settingsService.findByUserId(userId);
        if (settings == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(settings);
    }

    @GetMapping
    public ResponseEntity<List<Settings>> getAllSettings() {
        return ResponseEntity.ok(settingsService.findAll());
    }

}