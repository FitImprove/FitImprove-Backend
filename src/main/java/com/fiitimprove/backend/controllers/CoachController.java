package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.CoachSearchDTO;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.services.CoachService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {
    @Autowired
    private CoachService coachService;
    @PostMapping("/create")
    public ResponseEntity<Coach> createCoach(@RequestBody Coach coach) {
        return ResponseEntity.ok(coachService.createCoach(coach));
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<Coach>> getAllCoaches() {
        return ResponseEntity.ok(coachService.findAllCoaches());
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestBody CoachSearchDTO data) {
        return ResponseEntity.ok(coachService.search(data));
    }
}
