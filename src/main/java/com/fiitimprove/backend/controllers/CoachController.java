package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.SearchCoachDTO;
import com.fiitimprove.backend.fabric.SearchCoachFabric;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.CoachService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles endpoints related to coach creating and search
 */
@RestController
@RequestMapping("/api/coaches")
public class CoachController {
    @Autowired
    private SecurityUtil securityUtil;
    private final CoachService coachService;
    private SearchCoachFabric coachSearchFab;

    public CoachController(CoachService coachService, SearchCoachFabric coachSearch) {
        this.coachService = coachService;
        this.coachSearchFab = coachSearch;
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
    @Operation(summary = "Search for coaches based on name, gender and field. If some input is empty then it is not used", description = "Searches for coaches based on the provided criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of matching coaches retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<SearchCoachDTO>> search(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) User.Gender gender,
        @RequestParam(required = false) String field) 
    {
        securityUtil.getCurrentUserId();
        return ResponseEntity.ok(coachSearchFab.createList(coachService.search(name, field, gender)));
    }
}