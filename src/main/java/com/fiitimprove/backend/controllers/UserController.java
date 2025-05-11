package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.AuthentificationResponse;
import com.fiitimprove.backend.exceptions.AccessDeniedException;
import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.NotificationUpdateRequest;
import com.fiitimprove.backend.requests.SignInRequest;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.requests.UserUpdateProfileRequest;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.JwtService;
import com.fiitimprove.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API for managing users and coaches")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user (RegularUser or Coach) and returns an authentication token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthentificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            User crUser = userService.signup(user);
            System.out.println(crUser.toString());
            System.out.println("hi2");
            AuthentificationResponse authentificationResponse = jwtService.signUp(crUser);
            System.out.println("hi3");
            return ResponseEntity.ok(authentificationResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/signIn")
    @Operation(summary = "Sign in a user", description = "Authenticates a user with email and password and returns an authentication token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully signed in",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthentificationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> userSignIn(@RequestBody SignInRequest signInRequest) {
        try {
            AuthentificationResponse userLoginResponse = jwtService.signIn(signInRequest);
            return ResponseEntity.ok(userLoginResponse);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Get authenticated user data", description = "Returns the data of the currently authenticated user. Requires a valid JWT token in the Authorization header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> getUserData() {
        try {
            User user = securityUtil.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed to get user data: " + ex.getMessage());
        }
    }
    @GetMapping("/{userId}")
    @Operation(summary = "Get authenticated user data", description = "Returns the data of the currently authenticated user. Requires a valid JWT token in the Authorization header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> getAccurateUser(@PathVariable Long userId) {
        try {
           securityUtil.getCurrentUserId();
           return ResponseEntity.ok(userService.findById(userId));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed to get user data: " + ex.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Update user profile", description = "Updates the profile of the authenticated user. Only the user can update their own profile. Requires a valid JWT token in the Authorization header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot update another user's profile",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> updateUser(
            @RequestBody UserUpdateProfileRequest updateRequest) {
        try {
            System.out.println("som tu");

            Long currentUserId = securityUtil.getCurrentUserId();
            /*if (!currentUserId.equals(id)) {
                throw new AccessDeniedException("You can only update your own profile");
            }*/
            System.out.println("som tu" + currentUserId + " " + updateRequest);
            User updatedUser = userService.updateUser(currentUserId, updateRequest);
            System.out.println("u4");
            return ResponseEntity.ok(updatedUser);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the user: " + ex.getMessage());
        }
    }
    @GetMapping("/getAll")
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
    @PutMapping("/notifications")
    public ResponseEntity<?> updateNotifications(
            @RequestBody NotificationUpdateRequest request) {
        try {
            Long currentUserId = securityUtil.getCurrentUserId();
            userService.updateNotifications(currentUserId, request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating notifications: " + ex.getMessage());
        }
    }

}