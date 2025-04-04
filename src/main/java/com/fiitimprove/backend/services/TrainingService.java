package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.repositories.CoachRepository;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import com.fiitimprove.backend.repositories.TrainingRepository;
import com.fiitimprove.backend.repositories.TrainingUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Training createTraining(Long coachId, Training training, List<Long> invitedUserIds) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found with id: " + coachId));
        training.setCoach(coach);
        Training savedTraining = trainingRepository.save(training);

        if (training.getForType() == Training.ForType.LIMITED && invitedUserIds != null) {
            for (Long userId : invitedUserIds) {
                RegularUser user = regularUserRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                TrainingUser trainingUser = new TrainingUser();
                trainingUser.setTraining(savedTraining);
                trainingUser.setUser(user);
                trainingUser.setStatus(TrainingUser.Status.INVITED);
                trainingUserRepository.save(trainingUser);
            }
        }

        return savedTraining;
    }

    public List<Training> getTrainingsByCoachId(Long coachId) {
        return trainingRepository.findByCoachId(coachId);
    }


}