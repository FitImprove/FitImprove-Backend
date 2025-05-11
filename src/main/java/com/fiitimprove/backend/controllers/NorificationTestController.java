package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.NotificationRequest;
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
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/set-token")
    public ResponseEntity<?> setToken(@RequestBody String expoToken) {
        User user = securityUtil.getCurrentUser();
        notification.setToken(expoToken, user);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/send/{token}")
    public ResponseEntity<?> send(@PathVariable String token) {
        System.out.println("Got token: " + token);
        notification.sendNotification(String.format("ExponentPushToken[%s]", token), "It works from back", "Some sql injection");
        return ResponseEntity.ok(null);
    }
//    @PostMapping("/send")
//    public ResponseEntity<?> send(@RequestBody NotificationRequest request) {
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));
//
//        if (user.getPushtoken() == null) {
//            return ResponseEntity.badRequest().body("User has disabled notifications (pushtoken is null)");
//        }
//        System.out.println(user.getPushtoken());
//        System.out.println(request.getTitle());
//        System.out.println(request.getMessage());
//        System.out.println(String.format("ExponentPushToken[%s]", user.getPushtoken()));
//        notification.sendNotification(
//                user.getPushtoken(),
//                request.getTitle(),
//                request.getMessage()
//        );
//
//        return ResponseEntity.ok("Notification sent successfully");
//    }
}
