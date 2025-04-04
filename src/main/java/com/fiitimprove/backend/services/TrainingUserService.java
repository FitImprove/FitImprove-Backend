package com.fiitimprove.backend.services;

import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.repositories.TrainingRepository;
import com.fiitimprove.backend.repositories.TrainingUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TrainingUserService {

    @Autowired
    private TrainingUserRepository trainingUserRepository;

    @Autowired
    private TrainingRepository trainingRepository;


}