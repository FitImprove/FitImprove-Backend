package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.repositories.UserRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;


import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/notification")
public class NorificationTestController {
    @Autowired
    private NotificationService notification;
    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/set-token")
    @Operation(summary = "Allows user to set a push token that will be used to send a push notifications to a users device, can be set to null to disable notifications", description = "Sends a message in a specified chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Chat or sender not found")
    })
    public ResponseEntity<?> setToken(@RequestBody String expoToken) {
        User user = securityUtil.getCurrentUser();
        notification.setToken(expoToken, user);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/send/{token}")
    @Operation(summary = "Test api that sends test push notification to a device with specific token, token has to be sent without ExponentPushToken[], only the part incide of brackets", description = "Sends a message in a specified chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Chat or sender not found")
    })
    public ResponseEntity<?> send(@PathVariable String token) {
        System.out.println("Got token: " + token);
        notification.sendNotification(String.format("ExponentPushToken[%s]", token), "It works from back", "Some sql injection");
        return ResponseEntity.ok(null);
    }
}