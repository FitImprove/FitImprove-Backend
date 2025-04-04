package com.fiitimprove.backend.repositories;

import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.TrainingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.fiitimprove.backend.models.TrainingUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingUserRepository extends JpaRepository<TrainingUser, Long> {

}