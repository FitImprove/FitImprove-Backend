package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.TrainingUserDTO;
import com.fiitimprove.backend.exceptions.IncorrectDataException;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.models.TrainingUser.Status;
import com.fiitimprove.backend.repositories.TrainingRepository;
import com.fiitimprove.backend.repositories.TrainingUserRepository;
import com.fiitimprove.backend.repositories.RegularUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Service class for managing the enrollment and participation
 * of users in trainings.
 * Provides methods for enrolling users, cancelling reservations,
 * accepting or denying invitations, and retrieving training participation records
 */
@Service
public class TrainingUserService {
    @Autowired
    private TrainingUserRepository trainingUserRepository;
    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private RegularUserRepository regularUserRepository;
    @Autowired
    private NotificationService notificationService;

    /**
     * Retrieves all trainings that a user is currently enrolled in or invited to.
     * @param userId the ID of the user
     * @return a list of TrainingUser entities associated with the user
     */
    public List<TrainingUser> getAllEntoledTrainings(Long userId) {
        return trainingUserRepository.findByUserId(userId);
    }

    /**
     * Enrolls a user in a training with status AGREED.
     * @param trainingId the ID of the training
     * @param userId the ID of the user
     * @return the created TrainingUser enrollment
     * @throws Exception if the training or user is not found or validation fails
     */
    public TrainingUser enrollUserInTraining(Long trainingId, Long userId) throws Exception {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new ResourceNotFoundException("Training not found"));
        RegularUser user = regularUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return this.create(training, user, Status.AGREED);
    }

     /**
     * Cancels a user's reservation in a training.
     * @param trainingId the ID of the training
     * @param userId the ID of the user
     * @return the updated TrainingUser reservation with status CANCELED
     */
    public TrainingUser cancelTraining(Long trainingId, Long userId) throws Exception {
        var reservations = trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(trainingId, userId, Arrays.asList(Status.AGREED));
        if (reservations.isEmpty())
            throw new ResourceNotFoundException("User does not have an reservation in provided training");
        TrainingUser reservation = reservations.get(0);
        assert(reservation.getStatus() == Status.AGREED);
        reservation.setStatus(Status.CANCELED);
        reservation.setCanceledAt(LocalDateTime.now());

        Training tr = reservation.getTraining();
        if (tr.getForType() == Training.ForType.EVERYONE || reservation.getInvitedAt() == null) {
            tr.setFreeSlots(tr.getFreeSlots() + 1);
            trainingRepository.save(tr);
        }
        
        trainingUserRepository.save(reservation);
        return reservation;
    }

    /**
     * Cancels a user's participation (either AGREED or INVITED) in a training. Designed to be used by coach
     * @param userId the ID of the user
     * @param trainingId the ID of the training
     * @return the updated TrainingUser reservation with status DENIED
     * @throws ResourceNotFoundException if no reservation with status AGREED or INVITED exists
     */
    public TrainingUser cancelParticipation(Long userId, Long trainingId) {
        var reservations = trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(trainingId, userId, Arrays.asList(Status.AGREED, Status.INVITED));
        if (reservations.isEmpty())
            throw new ResourceNotFoundException("User does not have an reservation in provided training");
        TrainingUser reservation = reservations.get(0);
        assert(reservation.getStatus() == Status.AGREED || reservation.getStatus() == Status.INVITED);
        reservation.setStatus(Status.DENIED);
        reservation.setCanceledAt(LocalDateTime.now());

        Training tr = reservation.getTraining();
        if (tr.getForType() == Training.ForType.EVERYONE || reservation.getInvitedAt() == null) {
            tr.setFreeSlots(tr.getFreeSlots() + 1);
            trainingRepository.save(tr);
        }
        
        trainingUserRepository.save(reservation);
        return reservation;
    }

    /**
     * Denies an invitation to a training.
     * 
     * @param trainingId the ID of the training
     * @param userId the ID of the user
     * @return the updated TrainingUser invitation with status DENIED
     */
    public TrainingUser denyInvitation(Long trainingId, Long userId) throws Exception {
        var reservations = trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(trainingId, userId, Arrays.asList(Status.INVITED));
        if (reservations.isEmpty())
            throw new IncorrectDataException("User does not have an invitation in provided training");
        TrainingUser reservation = reservations.get(0);
        reservation.setStatus(Status.DENIED);
        trainingUserRepository.save(reservation);

        Training tr = reservation.getTraining();
        if (tr.getForType() == Training.ForType.EVERYONE) {
            tr.setFreeSlots(tr.getFreeSlots() + 1);
            trainingRepository.save(tr);
        }
        return reservation; 
    }

    /**
     * Accepts an invitation to a training (internal method without any validation).
     * 
     * @param invitation the TrainingUser invitation to accept
     */
    private void acceptInvitationUnsafe(TrainingUser invitation) {
        invitation.setStatus(Status.AGREED);
        invitation.setBookedAt(LocalDateTime.now());
        trainingUserRepository.save(invitation);
    }

    /**
     * Accepts an invitation to a training, wrapper around "acceptInvitationUnsafe"
     * @param trainingId id of the training
     * @param userId id of the user
     * @return new TrainingUser instance, saved in the db
     */
    public TrainingUser acceptInvitation(Long trainingId, Long userId) throws Exception {
        var reservations = trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(trainingId, userId, Arrays.asList(Status.INVITED));
        if (reservations.isEmpty())
            throw new IncorrectDataException("User does not have an invitation in provided training");
        
        TrainingUser invitation = reservations.get(0);
        this.acceptInvitationUnsafe(invitation);
        return invitation;
    }

    /**
     * Creates a TrainingUser enrollment or invitation without any input validation
     * 
     * @param training the training entity
     * @param user the user entity
     * @param st the status (AGREED or INVITED)
     * @return the created TrainingUser entity
     */
    public TrainingUser createUnsafe(Training training, RegularUser user, Status st) {
        TrainingUser tu = new TrainingUser();
        tu.setTraining(training);
        tu.setUser(user);
        tu.setStatus(st);

        switch (st) {
            case Status.AGREED:
                tu.setBookedAt(LocalDateTime.now());
                training.setFreeSlots(training.getFreeSlots()-1);
                trainingRepository.save(training);
                break;
            case Status.INVITED:
                tu.setInvitedAt(LocalDateTime.now());
                notificationService.sendInvitation(user, training);
                break;
            default: break;
        }

        trainingUserRepository.save(tu);
        return tu;
    }

    /**
     * Creates a TrainingUser enrollment or invitation with validation.
     * 
     * @param training the training entity
     * @param user the user entity
     * @param st the status, must be INVITED or AGREED
     * @return the created or updated TrainingUser entity
     * @throws Exception if validation fails or data inconsistency is found
     */
    public TrainingUser create(Training training, RegularUser user, Status st) throws Exception {
        if (st != Status.INVITED && st != Status.AGREED) 
            throw new IncorrectDataException(String.format("Can not create training that is not INVITED or AGREED, provided: %s", st.name()));

        if (training.isCanceled()) 
            throw new IncorrectDataException("Cannot enroll in a canceled training");
        if (training.getFreeSlots() <= 0 && st == Status.AGREED) 
            throw new IncorrectDataException("No free slots available");
        if (training.getTime().isBefore(LocalDateTime.now())) 
            throw new IncorrectDataException("Can not enroll in training that already started/ended");

        var existing = trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(training.getId(), user.getId(), Arrays.asList(Status.AGREED, Status.INVITED));
        if (!existing.isEmpty()) {
            if (existing.size() > 1) 
                throw new Exception("Data damage, user is enrolled/invited in the same training twice");
            TrainingUser elem = existing.get(0);
            if (elem.getStatus() == Status.AGREED) 
                throw new IncorrectDataException("User is already enrolled in this training");
            if (st == Status.INVITED) 
                throw new IncorrectDataException("User already has invitation for provided training");
            this.acceptInvitationUnsafe(elem);
            return elem;
        }

        return this.createUnsafe(training, user, st);
    }

    /**
     * Retrieves attended trainings of a user within a specified time period.
     * 
     * @param userId the ID of the user
     * @param start the start datetime of the period
     * @param end the end datetime of the period
     * @return a list of TrainingUserDTO representing attended trainings
     */
    public List<TrainingUserDTO> getAttendedTrainings(Long userId, LocalDateTime start, LocalDateTime end) {
        List<TrainingUser> tus = trainingUserRepository.findTrainRecordsInTimePeriod(userId, start, end, TrainingUser.Status.AGREED);
        return TrainingUserDTO.createList(tus);
    }

    /**
     * Retrieves updates related to the user's trainings since a given time.
     * 
     * @param userId the ID of the user
     * @param time the cutoff datetime to retrieve updates from
     * @return a list of TrainingUserDTO that were updated or created in this time period
     */
    public List<TrainingUserDTO> getUpdates(Long userId, LocalDateTime time) {
        return TrainingUserDTO.createList(trainingUserRepository.getUpdates(userId, time));
    }
}