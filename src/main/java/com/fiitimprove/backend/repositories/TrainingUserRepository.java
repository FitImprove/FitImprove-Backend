package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.TrainingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for {@link TrainingUser} that allows to make operations over db
 */
public interface TrainingUserRepository extends JpaRepository<TrainingUser, Long> {
    List<TrainingUser> findByTrainingIdAndUserIdAndStatusIn(Long trainingId, Long userId, List<TrainingUser.Status> statuses);

    /**
     * Retrieves training participation records for a user within a specific time period and with a given status.
     *
     * @param userId the ID of the user
     * @param start the start time of the time period
     * @param end the end time of the time period
     * @param status the status to filter by
     * @return a list of {@link TrainingUser} records
     */
    @Query("SELECT tu FROM TrainingUser tu JOIN tu.training t WHERE tu.user.id = :userId AND t.time BETWEEN :start AND :end AND tu.status = :status")
    List<TrainingUser> findTrainRecordsInTimePeriod(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("status") TrainingUser.Status status
    );

     /**
     * Retrieves all {@link TrainingUser} records linked to trainings occurring within a given time period.
     *
     * @param start the start time of the period
     * @param end the end time of the period
     * @return a list of {@link TrainingUser} records
     */
    @Query(value = "SELECT tu.* FROM trainings t JOIN training_users tu ON tu.training_id = t.id WHERE t.time_date_and_time BETWEEN :start AND :end", nativeQuery = true)
    List<TrainingUser> findTraininingUsersInTimePeriod(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Retrieves all {@link TrainingUser} records for a given training session occurring after a specified time
     * and with a given set of statuses.
     *
     * @param trainingId the ID of the training
     * @param time the time threshold
     * @param status the list of statuses to filter by
     * @return a list of {@link TrainingUser} records
     */
    @Query("SELECT tu FROM TrainingUser tu JOIN tu.training t WHERE t.id = :trainingId AND t.time >= :time AND tu.status in :status")
    List<TrainingUser> findUsersInTraining(
        @Param("trainingId") Long trainingId,
        @Param("time") LocalDateTime time,
        @Param("status") List<TrainingUser.Status> status
    );

    /**
     * Retrieves updates to {@link TrainingUser} entries for a user since a given timestamp.
     * An update is considered if the entry was invited or canceled after the given time.
     *
     * @param userId the ID of the user
     * @param time   the time after which updates are considered
     * @return list of updated {@link TrainingUser} entries
     */
    @Query("SELECT tu FROM TrainingUser tu WHERE tu.user.id = :userId AND (tu.invitedAt > :time OR tu.canceledAt > :time)") // OR tu.bookedAt > :time
    List<TrainingUser> getUpdates(
        @Param("userId") Long userId,
        @Param("time") LocalDateTime time
    );
    List<TrainingUser> findByUserIdAndStatusIn(Long userId, List<TrainingUser.Status> statuses);
    List<TrainingUser> findByUserId(Long userId);
}