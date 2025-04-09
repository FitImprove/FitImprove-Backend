package com.fiitimprove.backend.controllers;


import com.fiitimprove.backend.dto.AuthentificationResponse;
import com.fiitimprove.backend.dto.SignInRequest;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.services.JwtService;
import com.fiitimprove.backend.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
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
    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
}
