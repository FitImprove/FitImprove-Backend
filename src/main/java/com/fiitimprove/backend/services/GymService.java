package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.Gym;
import com.fiitimprove.backend.repositories.CoachRepository;
import com.fiitimprove.backend.repositories.GymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
