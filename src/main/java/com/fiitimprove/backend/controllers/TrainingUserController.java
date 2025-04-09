package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.EnrollUserRequest;
import com.fiitimprove.backend.dto.TrainingUserDTO;
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
    public ResponseEntity<TrainingUserDTO> enrollInTraining(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        TrainingUser tu = trainingUserService.enrollUserInTraining(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(TrainingUserDTO.create(tu));
    }

    @PostMapping("/denyInvitation")
    public ResponseEntity<TrainingUserDTO> denyInvitation(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        TrainingUser tu = trainingUserService.denyInvitation(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(TrainingUserDTO.create(tu));
    }

    @PostMapping("/acceptInvitation")
    public ResponseEntity<TrainingUserDTO> acceptInvitation(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        TrainingUser tu = trainingUserService.acceptInvitation(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(TrainingUserDTO.create(tu));
    }

    @PostMapping("/cancel")
    public ResponseEntity<TrainingUserDTO> cancelTraining(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        TrainingUser tu = trainingUserService.cancelTraining(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(TrainingUserDTO.create(tu));
    }

    @GetMapping("/enrolled/all/{user_id}")
    public ResponseEntity<List<TrainingUserDTO>> getAllEntoledTrainings(@PathVariable("user_id") Long userId)  throws Exception {
        List<TrainingUser> trs = trainingUserService.getAllEntoledTrainings(userId);
        return ResponseEntity.ok( TrainingUserDTO.convertList(trs) );
    }
}