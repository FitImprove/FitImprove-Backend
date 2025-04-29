package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.PubTrainingDTO;
import com.fiitimprove.backend.dto.TrainingEditDTO;
import com.fiitimprove.backend.dto.TrainingId;
import com.fiitimprove.backend.dto.TrainingUserDTO;
import com.fiitimprove.backend.exceptions.AccessDeniedException;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new training", description = "Creates a new training for a coach with optional invited users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<Training> createTraining(
            @RequestParam Long coachId,
            @Valid @RequestBody Training training,
            @RequestParam(required = false) List<Long> invitedUserIds) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(coachId)) {
            throw new AccessDeniedException("You can only create trainings for yourself");
        }
        Training createdTraining = trainingService.createTraining(coachId, training, invitedUserIds);
        return ResponseEntity.ok(createdTraining);
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel a training", description = "Cancels a training by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training canceled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training not found")
    })
    public ResponseEntity<Training> cancelTraining(@Valid @RequestBody TrainingId trainingId) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        Training tr = trainingService.cancel(trainingId.getTrainingId());
        return ResponseEntity.ok(tr);
    }

    @PutMapping("/edit")
    @Operation(summary = "Edit a training", description = "Edits an existing training with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training not found")
    })
    public ResponseEntity<String> edit(@RequestBody TrainingEditDTO data) {
        Long currentUserId = securityUtil.getCurrentUserId();
        trainingService.edit(data);
        return ResponseEntity.ok("Data changed successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PubTrainingDTO> get(@PathVariable Long id) {
        var training = trainingService.get(id);
        if (!training.isPresent())
            return ResponseEntity.notFound().build();        
        return ResponseEntity.ok(PubTrainingDTO.create(training.get()));
    }

    @GetMapping("/get-available-trainings/{coachId}")
    @Operation(summary = "Retuns availabe trainings for a specific coach, training is considered available if it is not cancaled, has free slots and is later then now", description = "Edits an existing training with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "400", description = "Invalid training data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training not found")
    })
    public ResponseEntity<List<PubTrainingDTO>> getAvailableTrainings(@Valid @PathVariable Long coachId) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(trainingService.getAvailableTrainings(coachId));
    }

    @GetMapping("/get-upcoming-trainings")
    @Operation(summary = "Retuns a trainings for a coach that he has upcoming(future)", description = "Edits an existing training with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "400", description = "Invalid training data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training not found")
    })
    public ResponseEntity<?> getUpcomingTraining() {
        Long currentUserId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(trainingService.getUpcomingTraining(currentUserId));
    }

    @GetMapping("/coach/{coachId}")
    @Operation(summary = "Get trainings by coach ID", description = "Retrieves a list of trainings for a specific coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<Training>> getTrainingsByCoachId(@PathVariable Long coachId) {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(coachId)) {
            throw new AccessDeniedException("You can only access your own trainings");
        }
        List<Training> trainings = trainingService.getTrainingsByCoachId(coachId);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/get-enrolled/{trainingId}")
    @Operation(summary = "Returns users that are enrolled in a training", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<TrainingUserDTO>> getEnrolled(@Valid @PathVariable Long trainingId) {
        Long currentUserId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(trainingService.getEnrolledUsers(trainingId));
    }

    @GetMapping("/updates")
    public ResponseEntity<List<PubTrainingDTO>> getUpdates(@RequestParam LocalDateTime time) {
        Long currentUserId = securityUtil.getCurrentUserId();
        System.err.printf("Update called for user: %d\n", currentUserId);
        return ResponseEntity.ok(trainingService.getUpdates(currentUserId, time));
    }
}