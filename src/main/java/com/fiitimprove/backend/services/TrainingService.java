package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.AlreadyClosedException;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.repositories.CoachRepository;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import com.fiitimprove.backend.repositories.TrainingRepository;
import com.fiitimprove.backend.repositories.TrainingUserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingService {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Autowired
    private TrainingUserRepository trainingUserRepository;

    @Autowired
    private TrainingUserService trainingUserService;

    @Transactional
    public Training createTraining(Long coachId, Training training, List<Long> invitedUserIds) throws Exception {
        var now = LocalDateTime.now();
        var time = training.getTimeDateAndTime();
        if (now.isAfter(time) || Duration.between(now, time).toMinutes() < 15)
            throw new AlreadyClosedException("The training can be crated at least 15 minutes before its begining");

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found with id: " + coachId));
        training.setCoach(coach);
        training.setCreatedAt(LocalDateTime.now());

        Training savedTraining = trainingRepository.save(training);
        if (invitedUserIds != null) {
            for (Long userId : invitedUserIds) {
                RegularUser user = regularUserRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                trainingUserService.createUnsafe(savedTraining, user, TrainingUser.Status.INVITED);
            }
        }

        return savedTraining;
    }

    public Training cancel(Long trainingId) throws Exception {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Training with id: %d not found", trainingId)));
        if (training.isCanceled())
            throw new AlreadyClosedException("Training is already canceled");
        
        for (TrainingUser user : training.getTrainingUsers()) {
            switch (user.getStatus()) {
                case TrainingUser.Status.AGREED:
                case TrainingUser.Status.INVITED:
                    user.setCanceledAt(LocalDateTime.now());
                    user.setStatus(TrainingUser.Status.CANCELED);
                default: break;
            }
        }
        training.setCanceled(true);

        trainingRepository.save(training);
        trainingUserRepository.saveAll(training.getTrainingUsers());
        return training;
    }

    public List<Training> getTrainingsByCoachId(Long coachId) {
        return trainingRepository.findByCoachId(coachId);
    }
}