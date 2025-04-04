package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.models.Gym;
import com.fiitimprove.backend.services.GymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gyms")
public class GymController {
    @Autowired
    private GymService gymService;

    @PostMapping("create/{coachId}")
    public ResponseEntity<Gym> createGym(@PathVariable Long coachId, @RequestBody Gym gym) {
        return ResponseEntity.ok(gymService.createGym(coachId, gym));
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<Gym> getGymByCoachId(@PathVariable Long coachId) {
        return ResponseEntity.ok(gymService.findByCoachId(coachId));
    }
}
