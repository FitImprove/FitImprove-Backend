package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.TrainingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingUserRepository extends JpaRepository<TrainingUser, Long> {
    List<TrainingUser> findByTrainingIdAndUserIdAndStatusIn(Long trainingId, Long userId, List<TrainingUser.Status> statuses);
    List<TrainingUser> findByUserIdAndStatusIn(Long userId, List<TrainingUser.Status> statuses);
    List<TrainingUser> findByUserId(Long userId);
}