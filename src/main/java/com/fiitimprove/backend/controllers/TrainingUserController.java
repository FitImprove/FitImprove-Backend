package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.AttendanceDTO;
import com.fiitimprove.backend.dto.EnrollUserRequest;
import com.fiitimprove.backend.dto.TrainingUserDTO;
import com.fiitimprove.backend.exceptions.AccessDeniedException;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.TrainingUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training-users")
public class TrainingUserController {
    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private final TrainingUserService trainingUserService;
    public TrainingUserController(TrainingUserService trainingUserService) {
        this.trainingUserService = trainingUserService;
    }

    @PostMapping("/enroll")
    @Operation(summary = "Enroll user in a training", description = "Enrolls a user in a specified training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User enrolled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training or user not found")
    })
    public ResponseEntity<TrainingUserDTO> enrollInTraining(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(request.getUserId())) {
            throw new AccessDeniedException("You can only enroll yourself in a training");
        }
        TrainingUser tu = trainingUserService.enrollUserInTraining(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(TrainingUserDTO.create(tu));
    }

    @PostMapping("/denyInvitation")
    @Operation(summary = "Deny training invitation", description = "Denies an invitation to a training for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation denied successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training or user not found")
    })
    public ResponseEntity<TrainingUserDTO> denyInvitation(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(request.getUserId())) {
            throw new AccessDeniedException("You can only deny your own invitations");
        }
        TrainingUser tu = trainingUserService.denyInvitation(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(TrainingUserDTO.create(tu));
    }

    @PostMapping("/acceptInvitation")
    @Operation(summary = "Accept training invitation", description = "Accepts an invitation to a training for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation accepted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training or user not found")
    })
    public ResponseEntity<TrainingUserDTO> acceptInvitation(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(request.getUserId())) {
            throw new AccessDeniedException("You can only accept your own invitations");
        }
        TrainingUser tu = trainingUserService.acceptInvitation(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(TrainingUserDTO.create(tu));
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel training enrollment", description = "Cancels a user's enrollment in a training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training canceled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Training or user not found")
    })
    public ResponseEntity<TrainingUserDTO> cancelTraining(@Valid @RequestBody EnrollUserRequest request) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(request.getUserId())) {
            throw new AccessDeniedException("You can only cancel your own enrollments");
        }
        TrainingUser tu = trainingUserService.cancelTraining(request.getTrainingId(), request.getUserId());
        return ResponseEntity.ok(TrainingUserDTO.create(tu));
    }

    @GetMapping("/enrolled/all/{user_id}")
    @Operation(summary = "Get all enrolled trainings for a user", description = "Retrieves a list of all trainings a user is enrolled in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of enrolled trainings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<TrainingUserDTO>> getAllEnrolledTrainings(@PathVariable("user_id") Long userId) throws Exception {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("You can only view your own enrolled trainings");
        }
        List<TrainingUser> trs = trainingUserService.getAllEntoledTrainings(userId);
        return ResponseEntity.ok(TrainingUserDTO.convertList(trs));
    }

    @GetMapping("/get-attendance/{userId}/{start}/{end}")
    @Operation(summary = "Get attendance for a user", description = "Retrieves a list of attended trainings for a user within a specified date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of attended trainings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<AttendanceDTO>> getAttendance(
            @PathVariable Long userId,
            @PathVariable LocalDateTime start,
            @PathVariable LocalDateTime end) {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("You can only view your own attendance");
        }
        return ResponseEntity.ok(trainingUserService.getAttendedTrainings(userId, start, end));
    }
}