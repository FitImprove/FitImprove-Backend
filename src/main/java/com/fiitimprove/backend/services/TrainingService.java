package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.PubTrainingDTO;
import com.fiitimprove.backend.dto.TrainingEditDTO;
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

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Service class responsible for managing Trainings and related operations.
 * <p>
 * This includes creating trainings, canceling trainings, editing training details,
 * retrieving trainings for coaches and users, and managing invited/enrolled users.
 * </p>
 */
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

    /**
     * Creates a new training session for the specified coach with optional invited users.
     * The training must be scheduled at least 15 minutes in the future.
     *
     * @param coachId        The ID of the coach creating the training.
     * @param training       The Training object to be created.
     * @param invitedUserIds List of user IDs to invite to the training. Can be null.
     * @return The saved Training entity.
     * @throws AlreadyClosedException   If the training time is in the past or less than 15 minutes from now.
     * @throws ResourceNotFoundException If the coach or any invited user does not exist.
     * @throws Exception                 For any other errors during creation.
     */
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

    /**
     * Cancels an existing training session and updates statuses of associated users.
     *
     * @param trainingId The ID of the training to cancel.
     * @return The updated Training entity marked as canceled.
     * @throws ResourceNotFoundException If the training with the given ID does not exist.
     * @throws AlreadyClosedException    If the training is already canceled.
     */
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

    /**
     * Edits the details of an existing training session.
     *
     * @param data DTO containing the updated training data.
     * @return The updated Training entity.
     * @throws ResourceNotFoundException If the training to edit does not exist.
     * @throws IncorrectDataException    If the freeSlots field is negative.
     */
    public Training edit(TrainingEditDTO data) throws ResourceNotFoundException {
        if (data.getFreeSlots() < 0) 
            throw new IncorrectDataException("freeSlots field of class Training can not be negative");
        
        Training tr = trainingRepository.findById(data.getId()).orElseThrow(() -> 
            new ResourceNotFoundException(String.format("Cant find training with id: {}", data.getId())));

        tr.setTitle(data.getTitle());
        tr.setDescription(data.getDescription());
        tr.setFreeSlots(data.getFreeSlots());
        tr.setForType(data.getForType());
        tr.setType(data.getType());
        
        trainingRepository.save(tr);
        return tr;
    }

    /**
     * Retrieves a Training by its ID.
     *
     * @param id The ID of the training to retrieve.
     * @return An Optional containing the Training if found, or empty if not.
     */
    public Optional<Training> get(Long id) {
        return trainingRepository.findById(id);
    }

    /**
     * Retrieves upcoming trainings for a given coach that are not canceled and have free slots.
     *
     * @param coachId The ID of the coach.
     * @return A list of public DTOs representing upcoming trainings.
     */
    public List<PubTrainingDTO> getUpcomingTraining(Long coachId) {
        List<Training> ts = trainingRepository.findByCoachIdAndTimeAfterAndIsCanceledAndFreeSlotsGreaterThan(coachId, LocalDateTime.now(), false, -1);
        return PubTrainingDTO.createList(ts);
    }

    /**
     * Retrieves available trainings for a given coach that start at least 5 minutes in the future,
     * are not canceled, and have free slots.
     *
     * @param coachId The ID of the coach.
     * @return A list of public DTOs representing available trainings.
     */
    public List<PubTrainingDTO> getAvailableTrainings(Long coachId) {
        List<Training> trs = trainingRepository.findByCoachIdAndTimeAfterAndIsCanceledAndFreeSlotsGreaterThan(coachId, LocalDateTime.now().plusMinutes(5), false, 0);
        return PubTrainingDTO.createList(trs);
    }

    /**
     * Retrieves all trainings associated with a given coach.
     *
     * @param coachId The ID of the coach.
     * @return A list of Training entities.
     */
    public List<Training> getTrainingsByCoachId(Long coachId) {
        return trainingRepository.findByCoachId(coachId);
    }

    /**
     * Retrieves users enrolled or invited to a training, filtered by status and current time.
     *
     * @param trainingId The ID of the training.
     * @return A list of TrainingUser entities with status AGREED or INVITED.
     */
    public List<TrainingUser> getEnrolledUsers(Long trainingId) {
        List<TrainingUser.Status> sts = new ArrayList<TrainingUser.Status>(2);
        sts.add(TrainingUser.Status.AGREED);
        sts.add(TrainingUser.Status.INVITED);
        List<TrainingUser> tu = trainingUserRepository.findUsersInTraining(trainingId, LocalDateTime.now(), sts);
        return tu;
    }

    /**
     * Retrieves updated trainings for a regular user since a specified time.
     *
     * @param userId The ID of the user.
     * @param time   The timestamp to filter updates.
     * @return A list of public DTOs representing updated trainings.
     */
    public List<PubTrainingDTO> getUpdatesRegularUser(Long userId, LocalDateTime time) {
        return PubTrainingDTO.createList(trainingRepository.getUpdatedRegularUser(userId, time));
    }

    /**
     * Retrieves updated trainings for a coach since a specified time.
     *
     * @param coachId The ID of the coach.
     * @param time    The timestamp to filter updates.
     * @return A list of public DTOs representing updated trainings.
     */
    public List<PubTrainingDTO> getUpdatesCoach(Long coachId, LocalDateTime time) {
        return PubTrainingDTO.createList(trainingRepository.getUpdatedCoach(coachId, time));
    }

    /**
     * Retrieves all trainings for a given coach.
     *
     * @param coachId The ID of the coach.
     * @return A list of Training entities.
     */
    public List<Training> getAllForCoach(Long coachId) {
        return trainingRepository.findByCoachId(coachId);
    }
}