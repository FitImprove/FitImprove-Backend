package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByCoachId(Long coachId);
}
