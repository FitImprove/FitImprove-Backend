package com.fiitimprove.backend.controllers;

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

@RestController
@RequestMapping("/api/notification")
public class NorificationTestController {
    @Autowired
    private NotificationService notification;
    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/set-token")
    public ResponseEntity<?> setToken(@RequestBody String expoToken) {
        User user = securityUtil.getCurrentUser();
        notification.setToken(expoToken, user);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/send/{token}")
    public ResponseEntity<?> send(@PathVariable String token) {
        notification.sendNotification(String.format("ExponentPushToken[%s]", token), "It works from back", "Some sql injection");
        return ResponseEntity.ok(null);
    }
}
