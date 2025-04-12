package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.exceptions.AccessDeniedException;
import com.fiitimprove.backend.models.Gym;
import com.fiitimprove.backend.requests.GymUpdateRequest;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.GymService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    private final GymService gymService;
    private final SecurityUtil securityUtil;
    public GymController(GymService gymService, SecurityUtil securityUtil) {
        this.gymService = gymService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("create/{coachId}")
    @Operation(summary = "Create a gym for a coach", description = "Creates a gym associated with a specified coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gym created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid gym data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach not found")
    })
    public ResponseEntity<Gym> createGym(@PathVariable Long coachId, @RequestBody Gym gym) {
        return ResponseEntity.ok(gymService.createGym(coachId, gym));
    }
    @DeleteMapping("/delete/{coachId}")
    @Operation(summary = "Delete gym for a coach", description = "Deletes the gym associated with a specified coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gym deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach or gym not found")
    })
    public ResponseEntity<Void> deleteGym(@PathVariable Long coachId) {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(coachId)) {
            throw new AccessDeniedException("You can only delete your own gym");
        }
        gymService.deleteGym(coachId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/coach/{coachId}")
    @Operation(summary = "Get gym by coach ID", description = "Retrieves the gym associated with a specified coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gym retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach or gym not found")
    })
    public ResponseEntity<Gym> getGymByCoachId(@PathVariable Long coachId) {
        return ResponseEntity.ok(gymService.findByCoachId(coachId));
    }
    @PutMapping("/update/{coachId}")
    @Operation(summary = "Update gym for a coach", description = "Updates the gym associated with a specified coach")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gym updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid gym data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Coach or gym not found")
    })
    public ResponseEntity<Gym> updateGym(@PathVariable Long coachId, @Valid @RequestBody GymUpdateRequest request) {
        Long currentUserId = securityUtil.getCurrentUserId();
        if (!currentUserId.equals(coachId)) {
            throw new AccessDeniedException("You can only update your own gym");
        }
        Gym updatedGym = gymService.updateGym(coachId, request);
        return ResponseEntity.ok(updatedGym);
    }
}