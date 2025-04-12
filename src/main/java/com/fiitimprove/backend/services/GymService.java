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

@Service
public class GymService {
    @Autowired
    private GymRepository gymRepository;
    @Autowired
    private CoachRepository coachRepository;

    public Gym createGym(Long coachId, Gym gym) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found with id: " + coachId));
        gym.setCoach(coach);
        return gymRepository.save(gym);
    }

    public Gym findByCoachId(Long coachId) {
        return gymRepository.findByCoachId(coachId)
                .orElseThrow(() -> new RuntimeException("Gym not found for coach with id: " + coachId));
    }
    public Gym updateGym(Long coachId, GymUpdateRequest request) {
        Gym gym = gymRepository.findByCoachId(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found for coach with id: " + coachId));
        gym.setName(request.getName());
        gym.setLatitude(request.getLatitude());
        gym.setLongitude(request.getLongitude());
        gym.setAddress(request.getAddress());
        return gymRepository.save(gym);
    }
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
