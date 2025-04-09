package com.fiitimprove.backend.services;

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

@Service
public class TrainingUserService {

    @Autowired
    private TrainingUserRepository trainingUserRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private RegularUserRepository regularUserRepository;

    public List<TrainingUser> getAllEntoledTrainings(Long userId) {
        return trainingUserRepository.findByUserId(userId);
    }

    public TrainingUser enrollUserInTraining(Long trainingId, Long userId) throws Exception {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new ResourceNotFoundException("Training not found"));
        RegularUser user = regularUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return this.create(training, user, Status.AGREED);
    }

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

    private void acceptInvitationUnsafe(TrainingUser invitation) {
        invitation.setStatus(Status.AGREED);
        invitation.setBookedAt(LocalDateTime.now());
        trainingUserRepository.save(invitation);
    }

    public TrainingUser acceptInvitation(Long trainingId, Long userId) throws Exception {
        var reservations = trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(trainingId, userId, Arrays.asList(Status.INVITED));
        if (reservations.isEmpty())
            throw new IncorrectDataException("User does not have an invitation in provided training");
        
        TrainingUser invitation = reservations.get(0);
        this.acceptInvitationUnsafe(invitation);
        return invitation;
    }

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
                break;
            default: break;
        }

        trainingUserRepository.save(tu);
        return tu;
    }

    public TrainingUser create(Training training, RegularUser user, Status st) throws Exception {
        if (st != Status.INVITED && st != Status.AGREED) 
            throw new IncorrectDataException(String.format("Can not create training that is not INVITED or AGREED, provided: %s", st.name()));

        if (training.isCanceled()) 
            throw new IllegalStateException("Cannot enroll in a canceled training");
        if (training.getFreeSlots() <= 0 && st == Status.AGREED) 
            throw new IllegalStateException("No free slots available");
        if (training.getTimeDateAndTime().isBefore(LocalDateTime.now())) 
            throw new IllegalStateException("Can not enroll in training that already started/ended");

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
}