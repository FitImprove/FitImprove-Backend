package com.fiitimprove.backend.controllers;


import com.fiitimprove.backend.models.User;
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
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try{
            User crUser = userService.signup(user);
            return ResponseEntity.ok(crUser);
        } catch(Exception ex)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
}
