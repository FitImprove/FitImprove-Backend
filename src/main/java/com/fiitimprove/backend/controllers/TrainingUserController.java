package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.EnrollUserRequest;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.services.TrainingUserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training-users")
public class TrainingUserController {

    @Autowired
    private TrainingUserService trainingUserService;

    @PostMapping("/enroll")
    public ResponseEntity<TrainingUser> enrollInTraining(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        TrainingUser tu = trainingUserService.enrollUserInTraining(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(tu);
    }

    @PostMapping("/denyInvitation")
    public ResponseEntity<TrainingUser> denyInvitation(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        TrainingUser tu = trainingUserService.denyInvitation(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(tu);
    }

    @PostMapping("/acceptInvitation")
    public ResponseEntity<TrainingUser> acceptInvitation(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        TrainingUser tu = trainingUserService.acceptInvitation(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(tu);
    }

    @PostMapping("/cancel")
    public ResponseEntity<TrainingUser> cancelTraining(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        TrainingUser tu = trainingUserService.cancelTraining(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(tu);
    }

    @GetMapping("/enrolled/all/{user_id}")
    public ResponseEntity<List<TrainingUser>> getAllEntoledTrainings(@PathVariable("user_id") Long userId)  throws Exception {
        return ResponseEntity.ok(
            trainingUserService.getAllEntoledTrainings(userId) );
    }
}