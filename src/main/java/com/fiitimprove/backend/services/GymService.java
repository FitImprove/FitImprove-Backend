package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.Gym;
import com.fiitimprove.backend.repositories.CoachRepository;
import com.fiitimprove.backend.repositories.GymRepository;
import com.fiitimprove.backend.requests.GymUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Gym entities associated with Coaches
 */
@Service
public class GymService {
    @Autowired
    private GymRepository gymRepository;
    @Autowired
    private CoachRepository coachRepository;

    /**
     * Creates a new Gym associated with the given coach ID
     * @param coachId the ID of the coach to associate the gym with
     * @param gym     the Gym object containing details to be saved
     * @return the saved Gym entity
     * @throws RuntimeException if the coach with the given ID does not exist
     */
    public Gym createGym(Long coachId, Gym gym) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found with id: " + coachId));
        gym.setCoach(coach);
        return gymRepository.save(gym);
    }

    /**
     * Finds the Gym associated with a given coach ID
     * @param coachId the ID of the coach whose gym is to be found
     * @return the Gym entity associated with the coach
     * @throws RuntimeException if no gym is found for the coach with the given ID
     */
    public Gym findByCoachId(Long coachId) {
        return gymRepository.findByCoachId(coachId)
                .orElseThrow(() -> new RuntimeException("Gym not found for coach with id: " + coachId));
    }

    /**
     * Updates an existing Gym associated with the given coach ID or creates a new one if none exists
     * @param coachId the ID of the coach whose gym is to be updated
     * @param request the update request containing new gym data
     * @return the updated or newly created Gym entity
     * @throws ResourceNotFoundException if the coach with the given ID does not exist
     */
    @Transactional
    public Gym updateGym(Long coachId, GymUpdateRequest request) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id: " + coachId));
        Gym gym = gymRepository.findByCoachId(coachId).orElse(null);
        if (gym == null) {
            gym = new Gym();
            gym.setCoach(coach);
            coach.setGym(gym);
        }
        gym.setLatitude(request.getLatitude());
        gym.setLongitude(request.getLongitude());
        gym.setAddress(request.getAddress());

        gymRepository.save(gym);
        coachRepository.save(coach);
        return gym;
    }

    /**
     * Deletes the Gym associated with the given coach ID.
     * @param coachId the ID of the coach whose gym is to be deleted
     * @throws ResourceNotFoundException if no gym is found for the coach with the given ID
     */
    @Transactional
    public void deleteGym(Long coachId) {
        Gym gym = gymRepository.findByCoachId(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found for coach with id: " + coachId));
        Coach coach = gym.getCoach();
        coach.setGym(null);
        coachRepository.save(coach);
        gymRepository.delete(gym);
    }
}
