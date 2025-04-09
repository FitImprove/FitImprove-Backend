package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TrainingRepository extends JpaRepository<Training, Long> {
    Optional<Training> findById(Long traningId);
    List<Training> findByCoachId(Long coachId);
}
