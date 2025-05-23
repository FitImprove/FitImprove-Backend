package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.PubTrainingDTO;
import com.fiitimprove.backend.dto.PubUserForTrainingDTO;
import com.fiitimprove.backend.dto.TrainingEditDTO;
import com.fiitimprove.backend.dto.TrainingId;
import com.fiitimprove.backend.fabric.PubUserDTOFabric;
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

/**
 * Handles endpoints related to training, creation, canceling, editing, requesting
 */
@RestController
@RequestMapping("/api/trainings")
public class TrainingController {
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private final TrainingService trainingService;
    @Autowired
    private final PubUserDTOFabric userDtoFabric;

    public TrainingController(TrainingService trainingService, PubUserDTOFabric userDtoFabric) {
        this.trainingService = trainingService;
        this.userDtoFabric = userDtoFabric;
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
    public ResponseEntity<PubTrainingDTO> createTraining(
            @Valid @RequestBody Training training,
            @RequestParam(required = false) List<Long> invitedUserIds) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        System.out.println("Id: " + currentUserId);
        Training createdTraining = trainingService.createTraining(currentUserId, training, invitedUserIds);
        return ResponseEntity.ok(PubTrainingDTO.create(createdTraining));
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
    public ResponseEntity<PubTrainingDTO> edit(@RequestBody @Valid TrainingEditDTO data) {
        Training t = trainingService.edit(data);
        return ResponseEntity.ok(PubTrainingDTO.create(t));
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

    @GetMapping("/coach")
    @Operation(summary = "Get trainings by coach ID", description = "Retrieves a list of trainings for a specific coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<PubTrainingDTO>> getTrainingsByCoachId() {
        Long coachId = securityUtil.getCurrentUserId();
        List<Training> trainings = trainingService.getTrainingsByCoachId(coachId);
        return ResponseEntity.ok(PubTrainingDTO.createList(trainings));
    }

    @GetMapping("/get-enrolled/{trainingId}")
    @Operation(summary = "Returns users that are enrolled in a training", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<PubUserForTrainingDTO>> getEnrolled(@Valid @PathVariable Long trainingId) {
        return ResponseEntity.ok(userDtoFabric.createList(trainingService.getEnrolledUsers(trainingId)));
    }

    @GetMapping("/updates")
    @Operation(summary = "Returns updated trainings for regular user", description = "Returns a list of trainings that were created/change in the time since :time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<PubTrainingDTO>> getUpdatesRegularUser(@RequestParam LocalDateTime time) {
        Long currentUserId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(trainingService.getUpdatesRegularUser(currentUserId, time));
    }
    @GetMapping("/updates-coach")
    @Operation(summary = "Returns updated trainings for coach", description = "Returns a list of trainings that were created/change in the time since :time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<List<PubTrainingDTO>> getUpdatesCoach(@RequestParam LocalDateTime time) {
        Long currentUserId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(trainingService.getUpdatesCoach(currentUserId, time));
    }

    @GetMapping("/all-trainings-coach")
    @Operation(summary = "Returns all trainings that coach had/will have", description = "Returns a list of trainings that coach has ever had or will have")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })    
    public ResponseEntity<List<PubTrainingDTO>> getAllForCoach() {
        Long currentUserId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(PubTrainingDTO.createList(trainingService.getAllForCoach(currentUserId)));
    }
}