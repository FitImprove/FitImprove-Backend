package com.fiitimprove.backend.services;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.client.RestTemplate;

import com.fiitimprove.backend.models.Message;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.models.Message.SenderRole;
import com.fiitimprove.backend.repositories.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

@Service
public class NotificationService {
    @Autowired
    private UserRepository userRep;

    private static final String EXPO_API_URL = "https://exp.host/--/api/v2/push/send";

    public void setToken(String expoToken, User user) {
        user.setPushtoken(expoToken);
        userRep.save(user);
    }

    public void sendNotification(String expoPushToken, String title, String body) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> notification = new HashMap<>();
        notification.put("to", expoPushToken);
        notification.put("title", title);
        notification.put("body", body);
        notification.put("sound", "default");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(notification, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_API_URL, request, String.class);
            System.out.println("Expo response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending Expo notification: " + e.getMessage());
        }
    }

    public void sendNewMessage(Message message) {
        User sender, receiver;
        if (message.getSenderRole() == SenderRole.COACH) {
            sender = message.getChat().getCoach();
            receiver = message.getChat().getRegularUser();
        }  else {
            sender = message.getChat().getRegularUser();
            receiver = message.getChat().getCoach();
        }
        if (receiver.getPushtoken() == null) return;
        CompletableFuture.runAsync(() -> {
            sendNotification(
                receiver.getPushtoken(),
                "New message from %s %s".formatted(sender.getName(), sender.getSurname()),
                message.getContent());
        });
    }

    public void sendInvitation(User user, Training train) {
        String token = user.getPushtoken();
        if (token == null) return;
        CompletableFuture.runAsync(() -> {
            sendNotification(token, "Training invitation", String.format(
                "You have been invited to a training\nCoach: %s\nTime: %s\nTraining title: %s", 
                    train.getCoach().getUsername(), train.getTime(), train.getTitle()));
        });
    }

    public void sendTrainingReminder(List<User> users, Training train) {
        for (User user : users) {
            String token = user.getPushtoken();
            if (token != null) {
                long timeDiff = Duration.between(LocalDateTime.now(), train.getTime()).toMinutes();
                CompletableFuture.runAsync(() -> {
                    sendNotification(token, "Upcoming training", String.format("Training \"%s\" is coming up in %ld, dont miss it", train.getTitle(), timeDiff));
                });
            }
        }
    }
}
