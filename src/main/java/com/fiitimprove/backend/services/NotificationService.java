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

/**
 * Provides methods to send different types of notifications to the user, uses push token from users table.
 * If there is no token specified message will not be sent
 */
@Service
public class NotificationService {
    @Autowired
    private UserRepository userRep;

    /**
     * Api of the push notificaiton service provider
     */
    private static final String EXPO_API_URL = "https://exp.host/--/api/v2/push/send";

    /**
     * Sets the push token to users settings, 
     * @param expoToken push token from expo-notifications, might be null to disable notifications
     * @param user user that will be reseiving notifications
     */
    public void setToken(String expoToken, User user) {
        user.setPushtoken(expoToken);
        userRep.save(user);
    }

    /**
     * Basic method to send any notification
     * @param expoPushToken push token to which mail would be sent 
     * @param title title of the notification
     * @param body body of the notification
     */
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

    /**
     * Function to test notification
     * @param message message that will be sent
     */
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

    /**
     * Wrapper sendNotification for training invitation
     * @param user user which will receive notification
     * @param train training about which notification will be sent
     */
    public void sendInvitation(User user, Training train) {
        String token = user.getPushtoken();
        if (token == null) return;
        CompletableFuture.runAsync(() -> {
            sendNotification(token, "Training invitation", String.format(
                "You have been invited to a training\nCoach: %s\nTime: %s\nTraining title: %s", 
                    train.getCoach().getUsername(), train.getTime(), train.getTitle()));
        });
    }

    /**
     * Wrapper around sendNotification that sends training remainder for user
     * @param user user that will receive remained
     * @param train training about whichc user will be remainded
     */
    public void sendTrainingReminder(User user, Training train) {
        String token = user.getPushtoken();
        if (token != null) {
            long timeDiff = Duration.between(LocalDateTime.now(), train.getTime()).toMinutes();
            CompletableFuture.runAsync(() -> {
                sendNotification(token, "Upcoming training", String.format("Training \"%s\" is coming up in %ld, dont miss it", train.getTitle(), timeDiff));
            });
        }
    }
}
