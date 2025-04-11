package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.models.Gym;
import com.fiitimprove.backend.services.GymService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    private final GymService gymService;

    public GymController(GymService gymService) {
        this.gymService = gymService;
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
}