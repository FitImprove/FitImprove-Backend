package com.fiitimprove.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fiitimprove.backend.repositories.TrainingUserRepository;
import com.fiitimprove.backend.services.NotificationService;
import com.fiitimprove.backend.models.TrainingUser;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Background {
    private LocalDateTime lastUpdateTime = LocalDateTime.now();

    @Autowired
    private TrainingUserRepository tuRepository;
    @Autowired
    private NotificationService notSer;

    @Scheduled(fixedRate = 60000)
    public void performScheduledTask() {
        LocalDateTime last = lastUpdateTime;
        LocalDateTime next = LocalDateTime.now().plusMinutes(60000*2);

        System.out.println("Background process");

        List<TrainingUser> tus = tuRepository.findTraininingUsersInTimePeriod(last, next);
        for (var t : tus) {
            notSer.sendTrainingReminder(t.getUser(), t.getTraining());
        }
        lastUpdateTime = next;
    }
}