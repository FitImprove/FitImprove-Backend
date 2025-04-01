package com.fiitimprove.backend.services;


import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.repositories.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    public Coach createCoach(Coach coach) {
        coach.setJoinedAt(LocalDate.now());
        coach.setVerified(false);
        return coachRepository.save(coach);
    }
    public List<Coach> findAllCoaches() {
        return coachRepository.findAll();
    }


}