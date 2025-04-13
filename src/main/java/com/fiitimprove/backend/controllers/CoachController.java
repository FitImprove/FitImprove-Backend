package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.CoachSearchDTO;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.CoachService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {
    @Autowired
    private SecurityUtil securityUtil;
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

    @GetMapping("/search")
    @Operation(summary = "Search for coaches", description = "Searches for coaches based on the provided criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of matching coaches retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> search(@RequestBody CoachSearchDTO data) {
        securityUtil.getCurrentUserId();
        return ResponseEntity.ok(coachService.search(data));
    }
}