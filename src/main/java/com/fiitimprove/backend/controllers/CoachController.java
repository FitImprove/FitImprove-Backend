package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.services.CoachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {
    private final CoachService coachService;

    public CoachController(CoachService coachService) {
        this.coachService = coachService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new coach", description = "Creates a new coach with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coach created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid coach data provided"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Coach> createCoach(@RequestBody Coach coach) {
        return ResponseEntity.ok(coachService.createCoach(coach));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get all coaches", description = "Retrieves a list of all coaches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of coaches retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<Coach>> getAllCoaches() {
        return ResponseEntity.ok(coachService.findAllCoaches());
    }
}