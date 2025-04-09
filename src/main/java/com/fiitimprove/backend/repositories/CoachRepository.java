package com.fiitimprove.backend.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fiitimprove.backend.models.Coach;
public interface CoachRepository extends JpaRepository<Coach, Long> {
    Optional<Coach> findById(Long coachId);
}