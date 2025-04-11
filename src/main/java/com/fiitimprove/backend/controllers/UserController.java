package com.fiitimprove.backend.controllers;


import com.fiitimprove.backend.dto.AuthentificationResponse;
import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.SignInRequest;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.requests.UserUpdateProfileRequest;
import com.fiitimprove.backend.services.JwtService;
import com.fiitimprove.backend.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try{
            User crUser = userService.signup(user);
            System.out.println("hi2");
            AuthentificationResponse authentificationResponse = jwtService.signUp(crUser);
            System.out.println("hi3");
            return ResponseEntity.ok(authentificationResponse);
        } catch(Exception ex)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    @PostMapping("/signIn")
    public ResponseEntity<?> userSignIn(@RequestBody SignInRequest signInRequest) {
        try {
            AuthentificationResponse userLoginResponse = jwtService.signIn(signInRequest);
            return ResponseEntity.ok(userLoginResponse);
        } catch(Exception ex)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUserData() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed to get user data: " + ex.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateProfileRequest updateRequest) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Long userId = user.getId();
            if (!userId.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only update your own profile");
            }

            User updatedUser = userService.updateUser(userId, updateRequest);
            System.out.println("u4");
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the user: " + ex.getMessage());
        }
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
}
