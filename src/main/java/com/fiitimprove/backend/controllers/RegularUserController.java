package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.RegularUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regular-users")
public class RegularUserController {
    @Autowired
    private SecurityUtil securityUtil;
    private final RegularUserService regularUserService;


    public RegularUserController(RegularUserService regularUserService) {
        this.regularUserService = regularUserService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new regular user", description = "Creates a new regular user with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Regular user created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<RegularUser> createRegularUser(@RequestBody RegularUser regularUser) {
        return ResponseEntity.ok(regularUserService.createRegularUser(regularUser));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get all regular users", description = "Retrieves a list of all regular users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of regular users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<RegularUser>> getAllRegularUsers() {
        return ResponseEntity.ok(regularUserService.findAllRegularUsers());
    }
}