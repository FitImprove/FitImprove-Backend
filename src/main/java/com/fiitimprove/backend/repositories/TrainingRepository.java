package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.TrainingUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TrainingRepository extends JpaRepository<Training, Long> {
    Optional<Training> findById(Long traningId);
    List<Training> findByCoachId(Long coachId);
    List<Training> findByCoachIdAndTimeAfterAndIsCanceledAndFreeSlotsGreaterThan(Long coachId, LocalDateTime from, boolean isCanceled, int minFreeSlots);

    @Query(value = "SELECT t.* FROM training_users tu JOIN trainings t ON t.id = tu.training_id WHERE tu.user_id = :userId AND t.last_updated > :time", nativeQuery = true)
    List<Training> getUpdated(
        @Param("userId") Long userId,
        @Param("time") LocalDateTime time
    );
}