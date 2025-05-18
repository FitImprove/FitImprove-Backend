package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Training;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Training} that allows to make operations over db
 */
public interface TrainingRepository extends JpaRepository<Training, Long> {
    Optional<Training> findById(Long traningId);
    List<Training> findByCoachId(Long coachId);
    List<Training> findByCoachIdAndTimeAfterAndIsCanceledAndFreeSlotsGreaterThan(Long coachId, LocalDateTime from, boolean isCanceled, int minFreeSlots);

    /**
     * Returns list of training for regular user that were create or changed for specific user
     * @param userId id of the user
     * @param time last update
     * @return trainings
     */
    @Query(value = "SELECT t.* FROM training_users tu JOIN trainings t ON t.id = tu.training_id WHERE tu.user_id = :userId AND t.last_updated > :time", nativeQuery = true)
    List<Training> getUpdatedRegularUser(
        @Param("userId") Long userId,
        @Param("time") LocalDateTime time
    );

    /**
     * Returns list of trainings for coach that changed or were created for specific coach
     * @param coachId 
     * @param time 
     * @return list of trainings
     */
    @Query(value = "SELECT t.* FROM trainings t WHERE t.coach_id = :coachId AND t.last_updated > :time", nativeQuery = true)
    List<Training> getUpdatedCoach(
        @Param("coachId") Long coachId,
        @Param("time") LocalDateTime time
    );
}