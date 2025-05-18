package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link Gym} that allows to make operations over db
 */
public interface GymRepository extends JpaRepository<Gym, Long> {
    Optional<Gym> findByCoachId(Long coachId);
}
