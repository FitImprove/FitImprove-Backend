package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.TrainingId;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.services.TrainingService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    @PostMapping("/create")
    public ResponseEntity<Training> createTraining(
            @RequestParam Long coachId,
            @RequestBody Training training,
            @RequestParam(required = false) List<Long> invitedUserIds) throws Exception {
        Training createdTraining = trainingService.createTraining(coachId, training, invitedUserIds);
        return ResponseEntity.ok(createdTraining);
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<Training>> getTrainingsByCoachId(@PathVariable Long coachId) {
        List<Training> trainings = trainingService.getTrainingsByCoachId(coachId);
        return ResponseEntity.ok(trainings);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Training> cancelTraining(@Valid @RequestBody TrainingId trainingId) throws Exception {
        Training tr = trainingService.cancel(trainingId.getTrainingId());
        return ResponseEntity.ok(tr);
    }
}