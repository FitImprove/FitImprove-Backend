package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.TrainingId;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.services.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new training", description = "Creates a new training for a coach with optional invited users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<Training> createTraining(
            @RequestParam Long coachId,
            @Valid @RequestBody Training training,
            @RequestParam(required = false) List<Long> invitedUserIds) throws Exception {
        Training createdTraining = trainingService.createTraining(coachId, training, invitedUserIds);
        return ResponseEntity.ok(createdTraining);
    }

    @GetMapping("/coach/{coachId}")
    @Operation(summary = "Get trainings by coach ID", description = "Retrieves a list of trainings for a specific coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<Training>> getTrainingsByCoachId(@PathVariable Long coachId) {
        List<Training> trainings = trainingService.getTrainingsByCoachId(coachId);
        return ResponseEntity.ok(trainings);
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel a training", description = "Cancels a training by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training canceled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training ID"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training not found")
    })
    public ResponseEntity<Training> cancelTraining(@Valid @RequestBody TrainingId trainingId) throws Exception {
        Training tr = trainingService.cancel(trainingId.getTrainingId());
        return ResponseEntity.ok(tr);
    }
}