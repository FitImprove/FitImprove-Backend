package com.fiitimprove.backend.controllers;


import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.services.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regular-users")
public class RegularUserController {

    @Autowired
    private RegularUserService regularUserService;

    @PostMapping("/create")
    public ResponseEntity<RegularUser> createRegularUser(@RequestBody RegularUser regularUser) {
        return ResponseEntity.ok(regularUserService.createRegularUser(regularUser));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<RegularUser>> getAllRegularUsers() {
        return ResponseEntity.ok(regularUserService.findAllRegularUsers());
    }

}