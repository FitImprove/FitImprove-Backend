package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.PubTrainingDTO;
import com.fiitimprove.backend.dto.TrainingEditDTO;
import com.fiitimprove.backend.dto.TrainingUserDTO;
import com.fiitimprove.backend.exceptions.AlreadyClosedException;
import com.fiitimprove.backend.exceptions.IncorrectDataException;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.repositories.CoachRepository;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import com.fiitimprove.backend.repositories.TrainingRepository;
import com.fiitimprove.backend.repositories.TrainingUserRepository;

import jakarta.mail.Quota.Resource;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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
        var time = training.getTime();
        if (now.isAfter(time) || Duration.between(now, time).toMinutes() < 15)
            throw new AlreadyClosedException("The training can be crated at least 15 minutes before its begining");

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id: " + coachId));
        training.setCoach(coach);
        training.setCreatedAt(LocalDateTime.now());

        Training savedTraining = trainingRepository.save(training);
        if (invitedUserIds != null) {
            for (Long userId : invitedUserIds) {
                RegularUser user = regularUserRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
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

    public Training edit(TrainingEditDTO data) throws ResourceNotFoundException {
        if (data.getFreeSlots() < 0) 
            throw new IncorrectDataException("freeSlots field of class Training can not be negative");
        
        Training tr = trainingRepository.findById(data.getId()).orElseThrow(() -> 
            new ResourceNotFoundException(String.format("Cant find training with id: {}", data.getId())));

        tr.setTitle(data.getTitle());
        tr.setDescription(data.getDescription());
        tr.setFreeSlots(data.getFreeSlots());
        tr.setForType(data.getType());
        
        trainingRepository.save(tr);
        return tr;
    }

    public List<PubTrainingDTO> getUpcomingTraining(Long coachId) {
        List<Training> ts = trainingRepository.findByCoachIdAndTimeAfterAndIsCanceledAndFreeSlotsGreaterThan(coachId, LocalDateTime.now(), false, -1);
        return PubTrainingDTO.createForList(ts);
    }

    public List<PubTrainingDTO> getAvailableTrainings(Long coachId) {
        List<Training> trs = trainingRepository.findByCoachIdAndTimeAfterAndIsCanceledAndFreeSlotsGreaterThan(coachId, LocalDateTime.now().plusMinutes(5), false, 0);
        return PubTrainingDTO.createForList(trs);
    }

    public List<Training> getTrainingsByCoachId(Long coachId) {
        return trainingRepository.findByCoachId(coachId);
    }

    public List<TrainingUserDTO> getEnrolledUsers(Long trainingId) {
        List<TrainingUser.Status> sts = new ArrayList<TrainingUser.Status>(2);
        sts.add(TrainingUser.Status.AGREED);
        sts.add(TrainingUser.Status.INVITED);
        List<TrainingUser> tu = trainingUserRepository.findUsersInTraining(trainingId, LocalDateTime.now(), sts);
        return TrainingUserDTO.convertList(tu);
    }
}