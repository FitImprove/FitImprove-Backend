package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.exceptions.AccessDeniedException;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.requests.SettingsUpdateRequest;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {


    private final SettingsService settingsService;
    private final SecurityUtil securityUtil;
    public SettingsController(SettingsService settingsService, SecurityUtil securityUtil) {
        this.settingsService = settingsService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/create/{userId}")
    @Operation(summary = "Create settings for a user", description = "Creates settings for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid settings data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Settings> createSettings(@PathVariable Long userId, @Valid @RequestBody Settings settings) {
        return ResponseEntity.ok(settingsService.createSettings(userId, settings));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get settings by user ID", description = "Retrieves settings for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Settings or user not found")
    })
    public ResponseEntity<Settings> getSettingsByUserId(@PathVariable Long userId) {
        Settings settings = settingsService.findByUserId(userId);
        if (settings == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(settings);
    }

    @GetMapping
    @Operation(summary = "Get all settings", description = "Retrieves a list of all settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of settings retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<Settings>> getAllSettings() {
        return ResponseEntity.ok(settingsService.findAll());
    }
    @PutMapping("/update")
    @Operation(summary = "Update settings for a user", description = "Updates settings for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid settings data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Settings or user not found")
    })
    public ResponseEntity<Settings> updateSettings( @Valid @RequestBody SettingsUpdateRequest request) {
        Long currentUserId = securityUtil.getCurrentUserId();
        System.out.println(currentUserId);
        Settings updatedSettings = settingsService.updateSettings(currentUserId, request);
        return ResponseEntity.ok(updatedSettings);
    }
}