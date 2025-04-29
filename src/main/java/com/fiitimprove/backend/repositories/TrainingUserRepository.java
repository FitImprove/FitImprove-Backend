package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.TrainingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainingUserRepository extends JpaRepository<TrainingUser, Long> {
    List<TrainingUser> findByTrainingIdAndUserIdAndStatusIn(Long trainingId, Long userId, List<TrainingUser.Status> statuses);
    // List<TrainingUser> findByUserIdAndTimeBetweenAndStatus(Long userId, LocalDateTime start, LocalDateTime end, TrainingUser.Status status);
    @Query("SELECT tu FROM TrainingUser tu JOIN tu.training t WHERE tu.user.id = :userId AND t.time BETWEEN :start AND :end AND tu.status = :status")
    List<TrainingUser> findTrainRecordsInTimePeriod(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("status") TrainingUser.Status status
    );

    @Query("SELECT tu FROM TrainingUser tu JOIN tu.training t WHERE t.id = :trainingId AND t.time >= :time AND tu.status in :status")
    List<TrainingUser> findUsersInTraining(
        @Param("trainingId") Long trainingId,
        @Param("time") LocalDateTime time,
        @Param("status") List<TrainingUser.Status> status
    );

    @Query("SELECT tu FROM TrainingUser tu WHERE tu.user.id = :userId AND (tu.invitedAt > :time OR tu.canceledAt > :time OR tu.bookedAt > :time)")
    List<TrainingUser> getUpdates(
        @Param("userId") Long userId,
        @Param("time") LocalDateTime time
    );

    List<TrainingUser> findByUserIdAndStatusIn(Long userId, List<TrainingUser.Status> statuses);
    List<TrainingUser> findByUserId(Long userId);
}